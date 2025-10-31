-- Create expenses table
CREATE TABLE expenses (
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL CHECK (amount > 0),
    paid_by BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    expense_date TIMESTAMP,
    category VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_expense_payer FOREIGN KEY (paid_by) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_expense_group FOREIGN KEY (group_id) 
        REFERENCES expense_groups(id) ON DELETE CASCADE
);

CREATE INDEX idx_expenses_group ON expenses(group_id);
CREATE INDEX idx_expenses_payer ON expenses(paid_by);
CREATE INDEX idx_expenses_date ON expenses(expense_date);
CREATE INDEX idx_expenses_category ON expenses(category);
