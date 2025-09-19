@echo off
echo "Fixing git and pushing changes..."
git add pom.xml
git add src/main/java/com/example/simple/HealthPlannerSimpleApplication.java
git commit -m "feat: Add automatic .env file loading with dotenv-java"
git push origin main
echo "Done!"
pause
