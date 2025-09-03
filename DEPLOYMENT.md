# Health Planner Backend - Deployment Guide

## Environment Variables Setup

When deploying to Render, you need to set these environment variables manually:

### 1. Database Configuration
- **Key**: `AIVEN_DB_PASSWORD`
- **Value**: Your actual Aiven database password

### 2. SendGrid Configuration
- **Key**: `SENDGRID_API_KEY`
- **Value**: Your actual SendGrid API key

- **Key**: `SENDGRID_FROM_EMAIL`
- **Value**: Your verified SendGrid email address

### 3. CORS Configuration
- **Key**: `APP_CORS_ALLOWED_ORIGIN`
- **Value**: `*` (or your frontend URL)

## How to Set Environment Variables in Render:

1. Go to your Render dashboard
2. Select your `health-planner-backend` service
3. Go to **Environment** tab
4. Click **Add Environment Variable**
5. Add each key-value pair above
6. Click **Save Changes**
7. Redeploy your service

## Expected Result:
- Your Spring Boot application will start successfully
- Database connection will work with Aiven
- SendGrid email service will be functional
- CORS will allow requests from any origin
- Health check at `/swagger` will pass

## Troubleshooting:
- If database connection fails, verify `AIVEN_DB_PASSWORD` is set correctly
- If emails don't send, check `SENDGRID_API_KEY` and `SENDGRID_FROM_EMAIL`
- If CORS errors occur, ensure `APP_CORS_ALLOWED_ORIGIN` is set to `*`
