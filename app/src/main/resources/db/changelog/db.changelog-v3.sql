-- Combine owner to name column
UPDATE projects
SET
    name = owner || '/' || name;

-- Remove the owner column
ALTER TABLE projects
    DROP COLUMN owner;
