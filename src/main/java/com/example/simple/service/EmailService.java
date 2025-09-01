package com.example.simple.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.simple.web.dto.EntryResponse;
import java.util.List;
import java.io.IOException;

@Service
public class EmailService {

    @Value("${sendgrid.api-key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from-email}")
    private String fromEmail;

    public void sendHealthPlanEmail(EntryResponse healthPlan) throws IOException {
        try {
            System.out.println("=== EMAIL DEBUG INFO ===");
            System.out.println("Attempting to send email to: " + healthPlan.email());
            System.out.println("Using SendGrid API key: " + sendGridApiKey.substring(0, 10) + "...");
            System.out.println("From email: " + fromEmail);
            System.out.println("API Key length: " + sendGridApiKey.length());
            System.out.println("API Key valid: " + (sendGridApiKey.startsWith("SG.") && sendGridApiKey.length() > 50));
            
            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();

            Email from = new Email(fromEmail);
            String subject = "Your Personalized Health Plan - Health Planner";
            Email to = new Email(healthPlan.email());
            
            System.out.println("=== EMAIL DETAILS ===");
            System.out.println("From Email: '" + fromEmail + "'");
            System.out.println("To Email: '" + healthPlan.email() + "'");
            System.out.println("Subject: '" + subject + "'");

            // Create HTML content
            String htmlContent = createHealthPlanEmailHtml(healthPlan);
            Content content = new Content("text/html", htmlContent);

            Mail mail = new Mail(from, subject, to, content);
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            System.out.println("=== SENDGRID RESPONSE ===");
            System.out.println("Status Code: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());
            System.out.println("Headers: " + response.getHeaders());
            
            // Check if email was actually sent
            if (response.getStatusCode() != 202) {
                System.err.println("SENDGRID ERROR: Status " + response.getStatusCode() + " - " + response.getBody());
                throw new RuntimeException("SendGrid returned status: " + response.getStatusCode() + " - " + response.getBody());
            } else {
                System.out.println("=== EMAIL SENT SUCCESSFULLY ===");
            }
        } catch (IOException e) {
            System.err.println("IO Error sending email: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw IOException
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    private String createHealthPlanEmailHtml(EntryResponse healthPlan) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append("<title>Your Health Plan</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; }");
        html.append(".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px; margin-bottom: 30px; }");
        html.append(".section { background: #f8f9fa; padding: 20px; margin: 20px 0; border-radius: 8px; border-left: 4px solid #667eea; }");
        html.append(".bmi-result { background: #e8f5e8; padding: 15px; border-radius: 8px; text-align: center; margin: 20px 0; }");
        html.append(".bmi-value { font-size: 24px; font-weight: bold; color: #2d5a2d; }");
        html.append(".bmi-category { background: #667eea; color: white; padding: 5px 15px; border-radius: 20px; display: inline-block; margin-top: 10px; }");
        html.append(".list { margin: 10px 0; }");
        html.append(".list li { margin: 8px 0; }");
        html.append(".footer { text-align: center; margin-top: 30px; padding: 20px; background: #f8f9fa; border-radius: 8px; font-size: 14px; color: #666; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class=\"header\">");
        html.append("<h1>Your Personalized Health Plan</h1>");
        html.append("<p>Transform your health journey with customized recommendations</p>");
        html.append("</div>");
        
        html.append("<div class=\"bmi-result\">");
        html.append("<h2>Your BMI Analysis</h2>");
        html.append("<div class=\"bmi-value\">").append(healthPlan.bmi()).append("</div>");
        html.append("<div class=\"bmi-category\">").append(healthPlan.bmiCategory()).append("</div>");
        html.append("<p><strong>Weight:</strong> ").append(healthPlan.weightKg()).append(" kg | <strong>Height:</strong> ").append(healthPlan.heightCm()).append(" cm</p>");
        html.append("</div>");
        
        html.append("<div class=\"section\">");
        html.append("<h3>Your Personalized Meal Plan</h3>");
        html.append("<ul class=\"list\">");
        html.append(createListItems(healthPlan.mealPlan()));
        html.append("</ul>");
        html.append("</div>");
        
        html.append("<div class=\"section\">");
        html.append("<h3>Your Workout Plan</h3>");
        html.append("<ul class=\"list\">");
        html.append(createListItems(healthPlan.workoutPlan()));
        html.append("</ul>");
        html.append("</div>");
        
        html.append("<div class=\"section\">");
        html.append("<h3>Health Tips</h3>");
        html.append("<ul class=\"list\">");
        html.append(createListItems(healthPlan.tips()));
        html.append("</ul>");
        html.append("</div>");
        
        html.append("<div class=\"section\">");
        html.append("<h3>Motivational Quotes</h3>");
        html.append("<ul class=\"list\">");
        html.append(createListItems(healthPlan.quotes()));
        html.append("</ul>");
        html.append("</div>");
        
        html.append("<div class=\"footer\">");
        html.append("<p>Generated on: ").append(healthPlan.createdAt().toString()).append("</p>");
        html.append("<p>Thank you for using Health Planner!</p>");
        html.append("<p>Stay committed to your health journey!</p>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");
        
        return html.toString();
    }

    private String createListItems(List<String> items) {
        if (items == null || items.isEmpty()) {
            return "<li>No items available</li>";
        }
        return items.stream()
                .map(item -> "<li>" + item + "</li>")
                .reduce("", String::concat);
    }
}
