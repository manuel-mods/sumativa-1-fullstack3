-- Add indexes to improve query performance

-- Index for username and email searches
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Index for topics by user_id
CREATE INDEX IF NOT EXISTS idx_topics_user_id ON topics(user_id);

-- Index for comments by topic_id and user_id
CREATE INDEX IF NOT EXISTS idx_comments_topic_id ON comments(topic_id);
CREATE INDEX IF NOT EXISTS idx_comments_user_id ON comments(user_id);

-- Index for user_roles by user_id and role_id
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role_id ON user_roles(role_id);