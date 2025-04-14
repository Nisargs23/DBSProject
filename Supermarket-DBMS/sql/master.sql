-- Master SQL file for Supermarket DBMS
-- This file runs all SQL scripts in the correct order

-- First, create the database schema
@schema.sql

-- Next, create the functions
@functions.sql

-- Then, create the procedures
@procedures.sql

-- Finally, create the triggers
@triggers.sql

-- Display completion message
PROMPT All database objects have been created successfully.