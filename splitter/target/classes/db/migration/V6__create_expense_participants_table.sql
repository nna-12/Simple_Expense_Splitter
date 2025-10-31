-- Create expense_participants table to track who is involved in each expense
CREATE TABLE expense_participants (
    expense_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (expense_id, user_id),
    CONSTRAINT fk_participant_expense FOREIGN KEY (expense_id) 
        REFERENCES expenses(id) ON DELETE CASCADE,
    CONSTRAINT fk_participant_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX idx_expense_participants_expense ON expense_participants(expense_id);
CREATE INDEX idx_expense_participants_user ON expense_participants(user_id);

-- Add comment for clarity
COMMENT ON TABLE expense_participants IS 'Tracks which users are involved in splitting each expense';
