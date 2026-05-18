-- active user email unique index
CREATE UNIQUE INDEX IF NOT EXISTS uk_p_user_email_active
ON p_user (email)
WHERE deleted_at IS NULL;

-- active user nickname unique index
CREATE UNIQUE INDEX IF NOT EXISTS uk_p_user_nickname_active
ON p_user (nickname)
WHERE deleted_at IS NULL;
