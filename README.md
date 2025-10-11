# KYK Yemek Menüsü Botu - WhatsApp Otomasyonu

Bu, **KYK (Kredi ve Yurtlar Kurumu)** yurtları için yemek menülerini yönetmek ve belirlenen bir tarihte otomatik olarak WhatsApp üzerinden göndermek amacıyla tamamen **Java** programlama dili kullanılarak geliştirilmiş kişisel bir masaüstü uygulamasıdır.

Bu proje, sıfırdan işlevsel bir Grafiksel Kullanıcı Arayüzü (GUI) oluşturmaya, olay yönetimini (event handling) sağlamaya ve **Java Selenium** ile tarayıcı otomasyonu mantığını uygulayarak güvenilir bir bot deneyimi yaratmaya odaklanmaktadır.

---

## Özellikler

* **Menü Yönetim Arayüzü:** **Java Swing** kullanılarak oluşturulmuş, kullanıcıların yemek listelerini tarihe göre girmesine, düzenlemesine ve `.csv` dosyası olarak kaydetmesine olanak tanıyan bir masaüstü arayüzü.
* **CSV Entegrasyonu:** Kaydedilmiş menüleri `.csv` dosyasından okuyarak arayüzdeki alanları otomatik olarak doldurma.
* **WhatsApp Otomasyonu:** **Selenium WebDriver** kullanarak WhatsApp Web'e otomatik olarak bağlanma, belirtilen kişi veya grubu bulma ve formatlanmış menü mesajını gönderme.
* **Zamanlayıcı ile Gönderim:** Mesajların anlık olarak veya gelecekteki bir tarih ve saate ayarlanarak otomatik olarak gönderilmesini sağlayan zamanlayıcı özelliği.
* **Kalıcı Oturum:** Chrome'un kullanıcı profili özelliğinden faydalanarak, her seferinde QR kod okutma gereksinimini ortadan kaldıran kalıcı WhatsApp Web oturumu.

---

## Proje Durumu ve Planlanan Özellikler (v1.0)

**Mevcut Durum:** Projenin ana fonksiyonları (arayüz, CSV işlemleri ve WhatsApp otomasyonu) tamamlanmıştır. Bu **v1.0** sürümü, kararlı ve tam işlevsel bir yapıya sahiptir.

### Sonraki Adımlar (Gelecek Planları)

* **Gelişmiş Hata Yönetimi:** WhatsApp arayüzünde gelecekte olabilecek değişikliklere karşı botu daha dayanıklı hale getirmek.
* **Ayarlar Menüsü:** Chrome profil yolu gibi ayarların arayüz üzerinden değiştirilebilmesi.
* **Çoklu Gönderim:** Aynı mesajın birden fazla gruba veya kişiye gönderilebilmesi için bir alıcı listesi oluşturma özelliği.
* **İşlem Günlüğü (Log):** Botun adımlarının (giriş yapıldı, mesaj gönderildi vb.) arayüz üzerinde bir log alanında gösterilmesi.

---

## Geliştirme Ortamı ve Teknolojiler

* **Dil:** Java
* **Kütüphaneler:**
    * **Java Swing/AWT:** Grafiksel Kullanıcı Arayüzü (GUI) için.
    * **Java Selenium:** Web tarayıcı otomasyonu için.
* **Geliştirme Ortamı (IDE):** Eclipse IDE / Visual Studio Code

---

## Katkıda Bulunma

Bu proje kişisel bir proje olduğu için dışarıdan katkılar şu an için kapalıdır.

Ancak projeyle veya kod yapısıyla ilgileniyorsanız, gelecekteki geliştirmeleri takip etmekten çekinmeyin. Hata bildirimi veya özellik geliştirme gibi konularda iş birliği yapmaktan memnuniyet duyarım.

---

## Uygulama Önizlemesi (Video/GIF)

GitHub README dosyaları doğrudan video (`.mp4` vb.) oynatmayı desteklemez. En iyi ve en yaygın yöntem, uygulamanızın kısa bir videosunu çekip bunu **animasyonlu bir GIF**'e dönüştürmektir.

Bunu yapmak için:
1.  Ekran kaydedici bir programla uygulamanızın menü oluşturma, zamanlayıcı kurma ve botun çalışması gibi kısımlarını gösteren kısa bir video çekin.
2.  **Ezgif.com** gibi "video to gif converter" aramasıyla bulabileceğiniz online siteleri kullanarak videonuzu `.gif` formatına çevirin.
3.  Oluşturduğunuz `.gif` dosyasını, projenizin ana klasörüne veya `images` gibi bir klasöre ekleyin ve normal bir resim gibi GitHub'a yükleyin.

Aşağıdaki `<-- ... -->` yorum satırlarını silip, parantez `()` içine kendi `.gif` dosyanızın GitHub'daki linkini yapıştırarak önizlemeyi ekleyebilirsiniz.


<p align="center">
  <img src="https://github.com/user-attachments/assets/a87534e8-3a05-4a99-94c0-806046c6ba61" alt="Uygulama Önizlemesi" width="700"/>
</p>
