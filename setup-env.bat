@echo off
echo Creating .env file with your actual credentials...
echo.

echo # Database Configuration > .env
echo AIVEN_DB_PASSWORD=your_actual_aiven_password_here >> .env
echo. >> .env
echo # SendGrid Configuration >> .env
echo SENDGRID_API_KEY=your_actual_sendgrid_api_key_here >> .env
echo SENDGRID_FROM_EMAIL=lamijabasalic2205@gmail.com >> .env

echo .env file created successfully!
echo.
echo Now you can safely push to GitHub without exposing sensitive data.
echo The .env file is already in .gitignore and will not be committed.
pause
