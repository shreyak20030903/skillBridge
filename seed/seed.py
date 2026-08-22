"""
Seed script for SkillBridge.

Loads people, skills, projects, companies and their relationships into
CognoDB from seed/data.json, using the official Neo4j Python driver with
parameterised Cypher (no string concatenation).

Usage:
    pip install neo4j
    export NEO4J_URI="bolt+s://<instance-id>.databases.cognodb.cloud"
    export NEO4J_USERNAME="cognodb"
    export NEO4J_PASSWORD="<your generated password>"
    python seed.py
"""

import json
import os
import sys
import uuid
from pathlib import Path

from neo4j import GraphDatabase

DATA_FILE = Path(__file__).parent / "data.json"


def load_data():
    with open(DATA_FILE, encoding="utf-8") as f:
        return json.load(f)


def clear_db(tx):
    tx.run("MATCH (n) DETACH DELETE n")


def create_constraints(tx):
    tx.run("CREATE CONSTRAINT person_id IF NOT EXISTS FOR (p:Person) REQUIRE p.id IS UNIQUE")
    tx.run("CREATE CONSTRAINT skill_name IF NOT EXISTS FOR (s:Skill) REQUIRE s.name IS UNIQUE")
    tx.run("CREATE CONSTRAINT project_id IF NOT EXISTS FOR (pr:Project) REQUIRE pr.id IS UNIQUE")
    tx.run("CREATE CONSTRAINT company_name IF NOT EXISTS FOR (c:Company) REQUIRE c.name IS UNIQUE")


def load_skills(tx, skills):
    tx.run(
        """
        UNWIND $rows AS row
        MERGE (s:Skill {name: row.name})
        SET s.id = coalesce(s.id, randomUUID()), s.category = row.category
        """,
        rows=skills,
    )


def load_companies(tx, companies):
    tx.run(
        """
        UNWIND $rows AS row
        MERGE (c:Company {name: row.name})
        SET c.id = coalesce(c.id, randomUUID()), c.industry = row.industry
        """,
        rows=companies,
    )


def load_projects(tx, projects):
    tx.run(
        """
        UNWIND $rows AS row
        MERGE (pr:Project {id: row.id})
        SET pr.name = row.name, pr.description = row.description,
            pr.startDate = row.startDate, pr.endDate = row.endDate
        """,
        rows=projects,
    )


def load_people(tx, people):
    rows = [
        {
            "id": p["id"],
            "name": p["name"],
            "title": p["title"],
            "bio": p["bio"],
            "location": p["location"],
            "mentorAvailable": p["mentorAvailable"],
            "yearsExperience": p["yearsExperience"],
        }
        for p in people
    ]
    tx.run(
        """
        UNWIND $rows AS row
        MERGE (p:Person {id: row.id})
        SET p.name = row.name, p.title = row.title, p.bio = row.bio,
            p.location = row.location, p.mentorAvailable = row.mentorAvailable,
            p.yearsExperience = row.yearsExperience
        """,
        rows=rows,
    )


def load_has_skill(tx, people):
    rows = [
        {"personId": p["id"], "skillName": s["skill"], "level": s["level"], "years": s["years"]}
        for p in people
        for s in p.get("skills", [])
    ]
    tx.run(
        """
        UNWIND $rows AS row
        MATCH (p:Person {id: row.personId})
        MATCH (s:Skill {name: row.skillName})
        MERGE (p)-[hs:HAS_SKILL]->(s)
        SET hs.level = row.level, hs.yearsExperience = row.years
        """,
        rows=rows,
    )


def load_worked_on(tx, people):
    rows = [
        {"personId": p["id"], "projectId": pr["project"], "role": pr["role"]}
        for p in people
        for pr in p.get("projects", [])
    ]
    tx.run(
        """
        UNWIND $rows AS row
        MATCH (p:Person {id: row.personId})
        MATCH (pr:Project {id: row.projectId})
        MERGE (p)-[w:WORKED_ON]->(pr)
        SET w.role = row.role
        """,
        rows=rows,
    )


def load_worked_at(tx, people):
    rows = [
        {
            "personId": p["id"],
            "companyName": c["company"],
            "role": c["role"],
            "startDate": c["startDate"],
            "endDate": c["endDate"],
        }
        for p in people
        for c in p.get("companies", [])
    ]
    if not rows:
        return
    tx.run(
        """
        UNWIND $rows AS row
        MATCH (p:Person {id: row.personId})
        MATCH (c:Company {name: row.companyName})
        MERGE (p)-[w:WORKED_AT]->(c)
        SET w.role = row.role, w.startDate = row.startDate, w.endDate = row.endDate
        """,
        rows=rows,
    )


def load_endorsements(tx, endorsements):
    tx.run(
        """
        UNWIND $rows AS row
        MATCH (endorser:Person {id: row.endorser})
        MATCH (endorsee:Person {id: row.endorsee})
        MERGE (endorser)-[e:ENDORSED {skillName: row.skillName}]->(endorsee)
        SET e.weight = row.weight, e.note = row.note
        """,
        rows=endorsements,
    )


def main():
    uri = os.environ.get("NEO4J_URI")
    user = os.environ.get("NEO4J_USERNAME", "cognodb")
    password = os.environ.get("NEO4J_PASSWORD")

    if not uri or not password:
        print("ERROR: set NEO4J_URI and NEO4J_PASSWORD environment variables first.", file=sys.stderr)
        sys.exit(1)

    data = load_data()
    driver = GraphDatabase.driver(uri, auth=(user, password))

    try:
        driver.verify_connectivity()
        with driver.session() as session:
            print("Clearing existing data...")
            session.execute_write(clear_db)
            print("Creating constraints...")
            session.execute_write(create_constraints)
            print("Loading skills, companies, projects...")
            session.execute_write(load_skills, data["skills"])
            session.execute_write(load_companies, data["companies"])
            session.execute_write(load_projects, data["projects"])
            print("Loading people...")
            session.execute_write(load_people, data["people"])
            print("Loading relationships (HAS_SKILL, WORKED_ON, WORKED_AT)...")
            session.execute_write(load_has_skill, data["people"])
            session.execute_write(load_worked_on, data["people"])
            session.execute_write(load_worked_at, data["people"])
            print("Loading endorsements...")
            session.execute_write(load_endorsements, data.get("endorsements", []))
        print("Seed complete.")
    finally:
        driver.close()


if __name__ == "__main__":
    main()
