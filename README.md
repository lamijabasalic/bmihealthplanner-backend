# Health Planner Backend

## Setup Instructions

### 1. Environment Variables
Create a `.env` file in the root directory with the following variables:

```bash
# Database Configuration
AIVEN_DB_PASSWORD=your_actual_aiven_password_here

# SendGrid Configuration
SENDGRID_API_KEY=your_actual_sendgrid_api_key_here
SENDGRID_FROM_EMAIL=your_verified_sendgrid_email_here
```

### 2. Application Configuration
Copy `application-template.yml` to `src/main/resources/application.yml` and update with your actual values.

### 3. Running the Application
```bash
mvn spring-boot:run
```

### 4. API Documentation
- Swagger UI: http://localhost:8080/swagger
- API Docs: http://localhost:8080/api-docs

## Security Notes
- Never commit `.env` files or `application.yml` with real credentials
- Use environment variables for sensitive data
- The `.gitignore` file excludes sensitive files from version control

## Docker Deployment

### Local Development
```bash
# Build and run with Docker Compose
docker-compose up --build
```

### Render Deployment
1. Push your code to GitHub
2. Connect your repository to Render
3. Set the following environment variables in Render:
   - `AIVEN_DB_PASSWORD`: Your Aiven database password
   - `SENDGRID_API_KEY`: Your SendGrid API key
   - `SENDGRID_FROM_EMAIL`: Your verified SendGrid email
4. Deploy using the `render.yaml` configuration

The application will be available at your Render URL.
