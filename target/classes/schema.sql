-- ============================================================================
-- Social Media Agent - Database Schema
-- PostgreSQL
-- ============================================================================

-- Users
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    subscription_tier VARCHAR(50) DEFAULT 'STARTER',
    api_quota_remaining INT DEFAULT 1000,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    preferences JSONB
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Social Accounts
CREATE TABLE IF NOT EXISTS social_accounts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    platform VARCHAR(50) NOT NULL,
    platform_user_id VARCHAR(255),
    access_token VARCHAR(500),
    refresh_token VARCHAR(500),
    expires_at TIMESTAMP,
    followers_count BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    metadata JSONB,
    UNIQUE(user_id, platform)
);

CREATE INDEX IF NOT EXISTS idx_social_user_platform ON social_accounts(user_id, platform);

-- Content Posts
CREATE TABLE IF NOT EXISTS content_posts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content_text TEXT,
    status VARCHAR(50) DEFAULT 'DRAFT',
    tone VARCHAR(50),
    language VARCHAR(10) DEFAULT 'en',
    scheduled_time TIMESTAMP,
    published_time TIMESTAMP,
    platform_post_ids JSONB,
    approval_status VARCHAR(50) DEFAULT 'PENDING',
    approver_id BIGINT,
    approval_time TIMESTAMP,
    approval_feedback TEXT,
    sentiment_score DECIMAL(5,4),
    brand_voice_score DECIMAL(5,4),
    performance_metrics JSONB,
    revision_history JSONB,
    content_fingerprint VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_content_user_status ON content_posts(user_id, status);
CREATE INDEX IF NOT EXISTS idx_content_scheduled_time ON content_posts(scheduled_time);
CREATE INDEX IF NOT EXISTS idx_content_approval_status ON content_posts(approval_status);
CREATE INDEX IF NOT EXISTS idx_content_user_created ON content_posts(user_id, created_at DESC);

-- Scheduled Jobs
CREATE TABLE IF NOT EXISTS scheduled_jobs (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL,
    platform VARCHAR(50) NOT NULL,
    scheduled_time TIMESTAMP NOT NULL,
    executed_time TIMESTAMP,
    status VARCHAR(50) DEFAULT 'PENDING',
    retry_count INT DEFAULT 0,
    last_retry_time TIMESTAMP,
    next_retry_time TIMESTAMP,
    error_log TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_jobs_status_time ON scheduled_jobs(status, scheduled_time);
CREATE INDEX IF NOT EXISTS idx_jobs_platform ON scheduled_jobs(platform);
CREATE INDEX IF NOT EXISTS idx_jobs_retry ON scheduled_jobs(next_retry_time) WHERE status = 'RETRY';

-- Comments & Engagement
CREATE TABLE IF NOT EXISTS comments_engagement (
    id BIGSERIAL PRIMARY KEY,
    platform_comment_id VARCHAR(255),
    platform_user_id VARCHAR(255),
    user_handle VARCHAR(255),
    post_id BIGINT,
    comment_text TEXT,
    timestamp TIMESTAMP,
    sentiment_score DECIMAL(5,4),
    emotion VARCHAR(50),
    intent VARCHAR(50),
    language VARCHAR(10),
    is_spam BOOLEAN DEFAULT false,
    flagged BOOLEAN DEFAULT false,
    platform VARCHAR(50),
    status VARCHAR(50) DEFAULT 'PENDING',
    assigned_user_id BIGINT,
    response_text TEXT,
    response_time TIMESTAMP,
    auto_reply_id VARCHAR(255),
    conversation_context JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_comments_post_status ON comments_engagement(post_id, status);
CREATE INDEX IF NOT EXISTS idx_comments_platform_user ON comments_engagement(platform_user_id, platform);
CREATE INDEX IF NOT EXISTS idx_comments_user_status ON comments_engagement(user_handle, status);

-- Analytics Metrics
CREATE TABLE IF NOT EXISTS analytics_metrics (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT,
    account_id BIGINT,
    platform VARCHAR(50),
    metric_type VARCHAR(100),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    value DECIMAL(15,2),
    breakdown_by_segment JSONB,
    platform_specific_data JSONB
);

CREATE INDEX IF NOT EXISTS idx_metrics_post_metric ON analytics_metrics(post_id, metric_type);
CREATE INDEX IF NOT EXISTS idx_metrics_timestamp ON analytics_metrics(timestamp);
CREATE INDEX IF NOT EXISTS idx_metrics_account_date ON analytics_metrics(account_id, timestamp DESC);

-- Approval Workflows
CREATE TABLE IF NOT EXISTS approval_workflows (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL UNIQUE,
    status VARCHAR(50) DEFAULT 'PENDING',
    created_by BIGINT,
    assigned_to BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date TIMESTAMP,
    completed_at TIMESTAMP,
    feedback_text TEXT,
    revisions_count INT DEFAULT 0,
    risk_score DECIMAL(5,4),
    audit_trail JSONB
);

CREATE INDEX IF NOT EXISTS idx_wf_status ON approval_workflows(status);
CREATE INDEX IF NOT EXISTS idx_wf_assigned_to ON approval_workflows(assigned_to);

-- Brand Guidelines
CREATE TABLE IF NOT EXISTS brand_guidelines (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    guideline_text TEXT,
    visual_guidelines JSONB,
    compliance_rules JSONB,
    version INT DEFAULT 1,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
