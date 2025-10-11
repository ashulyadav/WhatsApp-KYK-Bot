// Dosya: bot/WhatsappService.java
package bot;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WhatsappService {
    public void sendMessage(String hedefSohbetAdi, String message) throws Exception { // 'phone' parametresi kaldırıldı
        System.out.println("Kod başlatıldı: WhatsappService.java");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-data-dir=C:/Users/User/Desktop/WhatsApp KYK Bot/Python/ChromeProfile");
        options.addArguments("--profile-directory=Default");
        
        WebDriver driver = null;
        try {
            driver = new ChromeDriver(options);
            System.out.println("Tarayıcı başlatıldı.");

            driver.get("https://web.whatsapp.com/");
            System.out.println("WhatsApp Web ana sayfasına gidildi. Lütfen QR kodu okutun...");

            WebDriverWait loginWait = new WebDriverWait(driver, Duration.ofMinutes(3));
            loginWait.until(ExpectedConditions.presenceOfElementLocated(By.id("pane-side")));
            System.out.println("Giriş başarılı! Sohbet paneli algılandı.");
            
            System.out.println("'" + hedefSohbetAdi + "' adlı sohbet aranıyor...");
            WebDriverWait chatWait = new WebDriverWait(driver, Duration.ofSeconds(30));
            
            By chatSelector = By.xpath("//span[@title='" + hedefSohbetAdi + "']");
            
            WebElement chat = chatWait.until(ExpectedConditions.presenceOfElementLocated(chatSelector));
            
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", chat);
            Thread.sleep(500); 

            chat.click();
            System.out.println("Sohbet bulundu ve tıklandı.");

            WebDriverWait messageWait = new WebDriverWait(driver, Duration.ofSeconds(15));
            By messageBoxSelector = By.xpath("//footer//div[@role='textbox']");
            WebElement messageBox = messageWait.until(ExpectedConditions.elementToBeClickable(messageBoxSelector));
            System.out.println("Mesaj kutusu bulundu.");

            String[] lines = message.split("\n");
            for (int i = 0; i < lines.length; i++) {
                messageBox.sendKeys(lines[i]);
                if (i < lines.length - 1) {
                    messageBox.sendKeys(Keys.chord(Keys.SHIFT, Keys.ENTER));
                }
            }
            messageBox.sendKeys(Keys.ENTER);
            System.out.println("Mesaj başarıyla gönderildi.");

            System.out.println("Mesajın senkronize olması için 2 dakika bekleniyor...");
            Thread.sleep(120000); 

        } catch (Exception e) {
            System.err.println("WhatsApp otomasyonu sırasında bir hata oluştu: " + e.getMessage());
            e.printStackTrace();
            throw e; 
        } finally {
            if (driver != null) {
                driver.quit();
                System.out.println("Tarayıcı kapatıldı.");
            }
        }
    }
}