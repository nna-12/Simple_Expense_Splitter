-- Create expense_groups table
CREATE TABLE expense_groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_group_creator FOREIGN KEY (created_by) 
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_groups_creator ON expense_groups(created_by);
CREATE INDEX idx_groups_name ON expense_groups(name);
