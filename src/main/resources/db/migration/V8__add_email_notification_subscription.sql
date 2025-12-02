-- Add email notification subscription fields to users table
ALTER TABLE users
ADD COLUMN email_notifications_enabled BOOLEAN NOT NULL DEFAULT TRUE,
ADD COLUMN sns_subscription_arn VARCHAR(500),
ADD COLUMN email_subscription_confirmed BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN email_subscribed_at TIMESTAMP;

-- Create index for faster queries
CREATE INDEX idx_users_email_notifications ON users(email_notifications_enabled, email_subscription_confirmed);

-- Add comment for documentation
COMMENT ON COLUMN users.email_notifications_enabled IS 'Whether user wants to receive email notifications';
COMMENT ON COLUMN users.sns_subscription_arn IS 'AWS SNS subscription ARN for email notifications';
COMMENT ON COLUMN users.email_subscription_confirmed IS 'Whether user has confirmed email subscription via SNS';
COMMENT ON COLUMN users.email_subscribed_at IS 'Timestamp when user was subscribed to email notifications';

