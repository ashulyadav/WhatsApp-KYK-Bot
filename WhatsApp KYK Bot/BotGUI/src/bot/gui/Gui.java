package bot.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;

import bot.WhatsappGui;
import bot.csv.OpenAndSaveCSV;
import bot.csv.ReadCSV;
import bot.gui.elements.PlaceholderText;

public class Gui extends JFrame 
{
    private JCheckBox uploadModeCheckBox;
    private JPanel fileUploadPanel; // Dropzone
    
    // YENİ EKLENDİ: Menü giriş paneli referansı
    private JPanel menuEntryPanel; 
    
    private PlaceholderText tarihField;
    private LocalDate currentDate; // Uygulamanın anlık gösterdiği tarihi tutar
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    
    private List<PlaceholderText> kahvaltiFields; 
    private List<PlaceholderText> aksamYemegiFields;

    private File loadedCSVFile = null; // Yüklenen dosyanın referansı için

    private final OpenAndSaveCSV csvHandler = new OpenAndSaveCSV();
    private final ReadCSV csvReader = new ReadCSV();
    
    private List<String> getKahvaltiData() {
        List<String> data = new ArrayList<>();
        for (PlaceholderText field : kahvaltiFields) {
            data.add(field.getText());
        }
        return data;
    }

    private List<String> getAksamYemegiData() {
        List<String> data = new ArrayList<>();
        for (PlaceholderText field : aksamYemegiFields) {
            data.add(field.getText());
        }
        return data;
    }
    
    private final String[] SABIT_KAHVALTI = {
        "Siyah/Yeşil Zeytin", "Çeyrek Ekmek", "500 mL Su", "Çay/Bitki Çayı"
    };
    private final int DUZENLENEBILIR_KAHVALTI_SAYISI = 4;
    private final String[] SABIT_AKSAM_YEMEGI = {
    	"Çeyrek Ekmek", "500 mL Su"
    };
    private final int DUZENLENEBILIR_AKSAM_SAYISI = 7;
    private final Map<String, Integer> AKŞAM_YEMEĞİ_YAPISI = new LinkedHashMap<>() {{
        put("Çorba 1:", 1); put("Çorba 2:", 0); 
        put("Ana Yemek 1:", 1); put("Ana Yemek 2:", 0);
        put("Pilav/Makarna:", 1); put("Salata/Meze:", 1);
        put("Tatlı/Meyve:", 1); 
    //  put("Çeyrek Ekmek:", 2); 
    //  put("500 mL Su:", 2); 

    }};

 // Gui() constructor'ı
    public Gui() 
    {
        this.currentDate = LocalDate.now();
        
        setTitle("KYK Yemek Listesi Girişi"); 
        setSize(600, 750); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        
        try {
            URL iconURL = getClass().getResource("/images/AppLogo.png");
            if (iconURL != null) {
                Image originalImage = new ImageIcon(iconURL).getImage(); 
                
                // İkonu 32x32 piksel boyutuna getir ve yumuşak (smooth) ölçekleme kullan
                Image scaledImage = originalImage.getScaledInstance(
                    32, 32, Image.SCALE_SMOOTH
                );
                
                // JFrame'in ikonunu ayarla
                this.setIconImage(scaledImage); 
                
            } else {
                System.err.println("UYARI: /images/AppLogo.png dosya yolu bulunamadı!");
            }
        } catch (Exception e) {
            System.err.println("HATA: Uygulama ikonu yüklenemedi: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 1. Ana Paneli Oluştur
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout()); 
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); 

        // 2. Üst Bölümü (Tarih ve Mod) Oluştur ve Ekle
        JPanel ustBolumFrame = createTarihBolumu(); // Sadece bir kez çağrıldı
        
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.NORTH;
        mainPanel.add(ustBolumFrame, gbc);

        // 3. Menü Bölümünü Oluştur ve Ekle
        JPanel menuFrame = createYemekMenuleriBolumu();
        
        gbc.gridy = 1;
        gbc.weighty = 1.0; 
        gbc.fill = GridBagConstraints.BOTH; 
        mainPanel.add(menuFrame, gbc);

        add(mainPanel);
        setVisible(true);
        
        updateTarihField(this.currentDate);
        updateMenuMode(); 
    }
    
    private void updateTarihField(LocalDate date) {
        String formattedDate = date.format(DATE_FORMATTER);
        tarihField.setText(formattedDate);
        this.currentDate = date;
        
        // Tarih güncellendiğinde otomatik olarak menüyü yüklemeyi tetikle.
        loadMenuForCurrentDate(); 
    }
    
    private void loadMenuForCurrentDate() {
        String tarih = tarihField.getText();
        
        // CSV Okuyucu Sınıfımızdan menüyü çek
        List<String> menuData = csvReader.getMenuByDate(tarih);
        
        fillMenuFields(menuData);
    }
    
    private JPanel createTarihBolumu() {
        JPanel ustBolumFrame = new JPanel(new GridBagLayout()); 
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL; // Çoğu öğenin yatayda dolmasını sağlar
        
        // 1. Tarih Alanı (DATE INPUT)
        tarihField = new PlaceholderText(""); 
        tarihField.setColumns(10);
        tarihField.setEditable(true); 
        
        // Tarih alanına ENTER basıldığında OK işlevini ekleyelim (OK butonu kaldırıldığı için)
        tarihField.addActionListener(e -> {
            try {
                LocalDate inputDate = LocalDate.parse(tarihField.getText(), DATE_FORMATTER);
                updateTarihField(inputDate);
            } catch (java.time.format.DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Geçersiz tarih formatı! Lütfen DD-MM-YYYY formatını kullanın.", "Hata", JOptionPane.ERROR_MESSAGE);
                updateTarihField(currentDate); 
            }
        });

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; // Tüm kalan yatay alanı kaplaması için
        ustBolumFrame.add(tarihField, gbc); 

        // 2. "<" Butonu
        JButton prevButton = new JButton("<");
        prevButton.setMargin(new Insets(1, 4, 1, 4));
        prevButton.addActionListener(e -> {
            updateTarihField(currentDate.minusDays(1)); 
        });
        gbc.gridx = 1; gbc.gridy = 0; 
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        ustBolumFrame.add(prevButton, gbc);
        
        // 3. ">" Butonu
        JButton nextButton = new JButton(">");
        nextButton.setMargin(new Insets(1, 4, 1, 4));
        nextButton.addActionListener(e -> {
            updateTarihField(currentDate.plusDays(1)); 
        });
        gbc.gridx = 2; gbc.gridy = 0; 
        gbc.weightx = 0.0;
        ustBolumFrame.add(nextButton, gbc);
        
        // 4. CSV Modu CheckBox
        uploadModeCheckBox = new JCheckBox("CSV'den Menü Yükle");
        uploadModeCheckBox.addActionListener(e -> updateMenuMode());
        gbc.gridx = 3; gbc.gridy = 0;
        gbc.weightx = 0.0;
        ustBolumFrame.add(uploadModeCheckBox, gbc);

        // 5. YENİ: WhatsApp Otomasyon Arayüzünü Açma Butonu
        JButton whatsappButton = new JButton();
        
        try {
            // İkonu projenin kaynaklarından yükle (WhatsApp KYK Bot\Java\BotGUI\src\images.WhatsappButton.png)
            // JAR dosyasında bu yol: /images/WhatsappButton.png olacaktır
            URL iconURL = getClass().getResource("/images/BotLogo.png");
            if (iconURL != null) {
                Image iconImage = new ImageIcon(iconURL).getImage();
                // İkonu 24x24 boyuta ölçekle
                Image scaledIcon = iconImage.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                whatsappButton.setIcon(new ImageIcon(scaledIcon));
                whatsappButton.setMargin(new Insets(1, 1, 1, 1)); // Padding'i azalt
                whatsappButton.setText("Botu Başlat"); 
            } else {
                whatsappButton.setText("WA Bot"); // İkon bulunamazsa metin göster
                System.err.println("UYARI: /images/WhatsappButton.png bulunamadı. Metin butonu kullanılıyor.");
            }
        } catch (Exception e) {
            whatsappButton.setText("WA Bot Hata");
            e.printStackTrace();
        }
        
        whatsappButton.addActionListener(e -> {
        	// 1. Mevcut pencereyi gizle
            this.setVisible(false);
            
            // 2. Yeni WhatsappGui penceresini aç
            // 'this' ana Gui örneğini WhatsappGui'ye referans olarak göndeririz.
            new WhatsappGui(this); 
        });

        gbc.gridx = 4; gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        ustBolumFrame.add(whatsappButton, gbc);

        return ustBolumFrame;
    }

    private JPanel createYemekMenuleriBolumu() {
        JPanel menuFrame = new JPanel(new GridBagLayout());
        menuFrame.setBorder(BorderFactory.createTitledBorder("Günlük Menü"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; 
        
        // 1. Menü Giriş Paneli (Kahvaltı ve Akşam Yemeği)
        menuEntryPanel = createMenuEntryPanel(); // Referansı sakla
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weighty = 1.0; 
        menuFrame.add(menuEntryPanel, gbc);
        
        // 2. Dosya Yükleme Paneli (Başlangıçta gizli)
        fileUploadPanel = createFileUploadDropzone();
        
        // YENİ KONUM: Menü girişinin altında (gridy=1) ve dikeyde yer kaplaması için weightY=1.0 yapıldı.
        // menuEntryPanel'in weightY'si 1.0 olduğu için, bu panel göründüğünde onu yukarı itecektir.
        gbc.gridx = 0; gbc.gridy = 1; 
        gbc.weighty = 1.0; // Yükseklik genişlemesi için
        fileUploadPanel.setVisible(false); // Başlangıçta gizli
        menuFrame.add(fileUploadPanel, gbc);

        // Kaydet Butonu (En altta)
        // BUTON METNİ GÜNCELLENDİ ve SADELEŞTİRİLDİ
        JButton kaydetButton = new JButton("Menüyü CSV Olarak Kaydet (Gönderim WA Bot panelinden yapılır)");
        kaydetButton.addActionListener(e -> {
            
            String csvFilePath = null; // İşlem sonucunda kaydedilen/kullanılacak dosya yolu
            
            try {
                if (uploadModeCheckBox.isSelected()) {
                    // --- 1. CSV Yükleme Modu ---
                    if (loadedCSVFile == null) {
                        JOptionPane.showMessageDialog(this, "Lütfen önce bir CSV dosyasını sürükleyip bırakın.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    csvFilePath = csvHandler.getLoadedCSVPath(loadedCSVFile);
                    System.out.println("Yüklenen CSV kullanılacak: " + csvFilePath);
                    
                } else {
                    // --- 2. Manuel Giriş Modu ---
                    String tarih = tarihField.getText();
                    if (tarih.isEmpty() || tarih.equals(tarihField.getPlaceholder())) {
                        JOptionPane.showMessageDialog(this, "Lütfen bir tarih giriniz.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // CSV oluşturma ve kaydetme işlemini çağır
                    csvFilePath = csvHandler.saveManualDataToCSV(
                        tarih, 
                        getKahvaltiData(), 
                        getAksamYemegiData()
                    );
                    System.out.println("Manuel giriş CSV olarak kaydedildi.");
                }

                // --- 3. PYTHON İŞLEMİNİ BAŞLATMA KISMI KALDIRILDI ---
                // Bu işlev artık WhatsappGui'nin "Şimdi Gönder" butonuna taşınmıştır.

                JOptionPane.showMessageDialog(this, 
                    "Menü başarıyla CSV dosyasına kaydedildi. WhatsApp üzerinden göndermek için 'Botu Başlat' butonunu kullanın.", 
                    "Kaydedildi", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (IOException ex) {
                // Dosya kaydetme hatası
                JOptionPane.showMessageDialog(this, 
                    "Hata: CSV kaydı sırasında bir sorun oluştu.\n" + ex.getMessage(), 
                    "İşlem Hatası", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        GridBagConstraints gbc_button = new GridBagConstraints();
        gbc_button.gridx = 0; gbc_button.gridy = 2; // En alta taşındı
        gbc_button.weightx = 1.0;
        gbc_button.weighty = 0.0;
        gbc_button.fill = GridBagConstraints.HORIZONTAL;
        gbc_button.insets = new Insets(10, 5, 5, 5); 
        menuFrame.add(kaydetButton, gbc_button);

        return menuFrame;
    }
    
    /**
     * Kahvaltı ve Akşam Yemeği bölümlerini içeren ana giriş panelini oluşturur.
     */
    private JPanel createMenuEntryPanel() {
        // İçerik önceki gibi kalır, sadece ana panel oluşturulur
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; 

        // --- Kahvaltı Bölümü ---
        JLabel kahvaltiLabel = new JLabel("Kahvaltı Menüsü:");
        gbc.gridx = 0; gbc.gridy = 0; gbc.weighty = 0.0;
        panel.add(kahvaltiLabel, gbc);

        JPanel kahvaltiFrame = createKahvaltiBolumu();
        gbc.gridx = 0; gbc.gridy = 1; gbc.weighty = 0.0; 
        panel.add(kahvaltiFrame, gbc);

        // --- Akşam Yemeği Bölümü ---
        JLabel aksamYemegiLabel = new JLabel("Akşam Yemeği Menüsü:");
        gbc.gridx = 0; gbc.gridy = 2; gbc.weighty = 0.0;
        panel.add(aksamYemegiLabel, gbc);

        JPanel aksamYemegiContent = createAksamYemegiBolumu();
        JScrollPane aksamYemegiScrollPane = new JScrollPane(
            aksamYemegiContent, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        aksamYemegiScrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.weighty = 1.0; // Dikeyde genişleme
        panel.add(aksamYemegiScrollPane, gbc);

        return panel;
    }

    /**
     * CSV Sürükle-Bırak alanını oluşturur.
     */
    private JPanel createFileUploadDropzone() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("CSV Dosyası Yükle (Sürükle-Bırak)"),
            BorderFactory.createLineBorder(Color.GRAY, 2, true) // Köşeleri yuvarlatılmış kenarlık eklendi
        ));
        
        JTextArea dropText = new JTextArea("CSV Dosyasını buraya sürükleyip bırakın (Tarih, Kahvaltı..., Akşam Yemeği... formatında olmalıdır).");
        dropText.setEditable(false);
        dropText.setLineWrap(true);
        dropText.setWrapStyleWord(true);
        dropText.setBackground(new Color(240, 255, 240)); 
        dropText.setForeground(Color.DARK_GRAY);
        
        // Yükseklik kontrolü için dropText'i sarmalayalım
        JScrollPane scrollPane = new JScrollPane(dropText);
        scrollPane.setPreferredSize(new Dimension(500, 150)); // Sabit bir yükseklik verelim
        panel.add(scrollPane, BorderLayout.CENTER);

        // *** TransferHandler (Sürükle-Bırak Mantığı) ***
        panel.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            @Override
            public boolean importData(TransferSupport support) {
                if (!canImport(support)) return false;

                try {
                    @SuppressWarnings("unchecked")
                    List<File> files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    
                    if (files.isEmpty()) return false;
                    
                    File droppedFile = files.get(0);
                    
                    if (droppedFile.getName().toLowerCase().endsWith(".csv")) {
                        loadedCSVFile = droppedFile; // Dosya referansını sakla
                        dropText.setText("YÜKLENDİ: " + droppedFile.getAbsolutePath() + "\n\nŞimdi 'Kaydet' butonuna basarak menüyü yükleyebilirsiniz.");
                        return true;
                    } else {
                        loadedCSVFile = null;
                        dropText.setText("HATA: Lütfen sadece CSV formatında bir dosya yükleyin.");
                        return false;
                    }
                    
                } catch (Exception ex) {
                    loadedCSVFile = null;
                    dropText.setText("Yükleme Hatası: " + ex.getMessage());
                    return false;
                }
            }
        });
        
        return panel;
    }

    /**
     * Yükleme modu seçildiğinde (checkbox basılıysa) manuel giriş panelini gizler, yükleme panelini gösterir.
     */
    private void updateMenuMode() {
        boolean uploadModeSelected = uploadModeCheckBox.isSelected();
        
        // Manuel giriş panelini ve yükleme panelini yeni konumlarına göre ayarla
        if (menuEntryPanel != null) {
            menuEntryPanel.setVisible(!uploadModeSelected);
        }
        
        fileUploadPanel.setVisible(uploadModeSelected);
        
        // Panellerin görünürlüğü değiştiği için ana çerçeveyi yenile
        revalidate(); 
        repaint();
    }

    // --- Diğer yardımcı metotlar (Değişmedi) ---

    private JPanel createKahvaltiBolumu() {
        JPanel kahvaltiFrame = new JPanel(); 
        kahvaltiFrame.setLayout(new BoxLayout(kahvaltiFrame, BoxLayout.Y_AXIS));
        kahvaltiFields = new ArrayList<>();
        
        for (int i = 0; i < DUZENLENEBILIR_KAHVALTI_SAYISI; i++) {
            PlaceholderText entry = new PlaceholderText("Kahvaltı " + (i + 1)); 
            entry.setAlignmentX(Component.LEFT_ALIGNMENT);
            kahvaltiFrame.add(entry);
            kahvaltiFields.add(entry);
            if (i < DUZENLENEBILIR_KAHVALTI_SAYISI - 1) {
                kahvaltiFrame.add(Box.createVerticalStrut(5));
            }
        }
        kahvaltiFrame.add(Box.createVerticalStrut(10));
        for (int i = 0; i < SABIT_KAHVALTI.length; i++) {
            String val = SABIT_KAHVALTI[i];
            PlaceholderText fixedEntry = new PlaceholderText(""); 
            fixedEntry.setText(val);
            fixedEntry.setEditable(false);
            fixedEntry.setBackground(UIManager.getColor("control")); 
            fixedEntry.setAlignmentX(Component.LEFT_ALIGNMENT);
            kahvaltiFrame.add(fixedEntry);
            kahvaltiFields.add(fixedEntry);
            if (i < SABIT_KAHVALTI.length - 1) {
                kahvaltiFrame.add(Box.createVerticalStrut(5));
            }
        }
        return kahvaltiFrame;
    }
    
    private JPanel createAksamYemegiBolumu() {
        JPanel aksamYemegiContent = new JPanel(new GridBagLayout()); 
        aksamYemegiFields = new ArrayList<>();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; 
        int row = 0;

        // --- 1. DÜZENLENEBİLİR ALANLARI EKLEME ---
        for (Map.Entry<String, Integer> entry : AKŞAM_YEMEĞİ_YAPISI.entrySet()) {
            String labelText = entry.getKey();
            int spacingType = entry.getValue();
            int topPadding = (spacingType == 1) ? 10 : 1; 
            gbc.insets = new Insets(topPadding, 5, 1, 5); 
            
            String placeholderText = labelText.replace(":", "").trim(); 
            PlaceholderText field = new PlaceholderText(placeholderText);
            
            gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 1.0;
            aksamYemegiContent.add(field, gbc);
            aksamYemegiFields.add(field);
            
            // Bu döngüde artık sabit (spacingType == 2) öğe yok!
            row++;
        }
        
        // --- 2. SABİT ALANLARI EKLEME (Kahvaltıdaki gibi) ---
        gbc.insets = new Insets(10, 5, 1, 5); // Sabitlerden önce boşluk bırakalım.
        for (int i = 0; i < SABIT_AKSAM_YEMEGI.length; i++) {
            String val = SABIT_AKSAM_YEMEGI[i];
            PlaceholderText fixedEntry = new PlaceholderText(""); 
            fixedEntry.setText(val);
            fixedEntry.setEditable(false);
            fixedEntry.setBackground(UIManager.getColor("control")); 
            
            gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 1.0;
            aksamYemegiContent.add(fixedEntry, gbc);
            aksamYemegiFields.add(fixedEntry);
            
            // Bir sonraki sabit öğeden önce daha az boşluk
            gbc.insets = new Insets(5, 5, 1, 5); 
            row++;
        }

        gbc.gridy = row; gbc.weighty = 1.0; gbc.gridx = 0; gbc.gridwidth = 1; 
        aksamYemegiContent.add(Box.createVerticalGlue(), gbc);
        return aksamYemegiContent;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Gui();
        });
    }
    
    private void fillMenuFields(List<String> menuData) {
        if (menuData.isEmpty()) {
            // Eğer veri yoksa, tüm alanları temizle
            clearAllMenuFields();
            return;
        }

        // Toplam 17 menü alanı var (Kahvaltı fields + Akşam fields)
        int dataIndex = 0;
        
        // 1. Kahvaltı alanlarını doldur (Hem düzenlenebilir hem sabit olanlar dahil)
        // Dikkat: kahvaltiFields listesi hem düzenlenebilir hem de sabit alanları içerir.
        for (PlaceholderText field : kahvaltiFields) {
            if (dataIndex < menuData.size()) {
                field.setText(menuData.get(dataIndex));
                dataIndex++;
            }
        }

        // 2. Akşam Yemeği alanlarını doldur
        for (PlaceholderText field : aksamYemegiFields) {
            if (dataIndex < menuData.size()) {
                field.setText(menuData.get(dataIndex));
                dataIndex++;
            }
        }
    }

    private void clearAllMenuFields() {
        // Tüm alanları temizler, placeholder'ları geri getirir ve sabitleri yeniden yazar.

        // 1. Kahvaltı Alanları (Daha Sağlam Hale Getirildi)
        // Düzenlenebilir alan sayısı bilindiği için indis (index) tabanlı döngü kullanmak daha güvenli.
        for (int i = 0; i < kahvaltiFields.size(); i++) {
            PlaceholderText field = kahvaltiFields.get(i);
            
            // Eğer alan düzenlenebilirler listesindeyse (ilk DUZENLENEBILIR_KAHVALTI_SAYISI alan)
            if (i < DUZENLENEBILIR_KAHVALTI_SAYISI) {
                field.setText(""); // Sadece düzenlenebilir olanları temizle
            } 
            // Eğer alan sabitler listesindeyse, değerini yeniden ata
            else {
                // SABIT_KAHVALTI dizisindeki doğru indeksi bul: i - (düzenlenebilir alan sayısı)
                int sabitIndex = i - DUZENLENEBILIR_KAHVALTI_SAYISI;
                if (sabitIndex < SABIT_KAHVALTI.length) {
                    field.setText(SABIT_KAHVALTI[sabitIndex]);
                }
            }
        }
        
        // 2. Akşam Yemeği Alanları (SORUNU ÇÖZEN DÜZELTME)
        // Kahvaltıdakiyle aynı mantık burada da uygulanır.
        for (int i = 0; i < aksamYemegiFields.size(); i++) {
            PlaceholderText field = aksamYemegiFields.get(i);
            
            // Eğer alan düzenlenebilirler listesindeyse (ilk DUZENLENEBILIR_AKSAM_SAYISI alan)
            if (i < DUZENLENEBILIR_AKSAM_SAYISI) {
                field.setText(""); // Sadece düzenlenebilir olanları temizle
            } 
            // Eğer alan sabitler listesindeyse, değerini yeniden ata
            else {
                // SABIT_AKSAM_YEMEGI dizisindeki doğru indeksi bul: i - (düzenlenebilir alan sayısı)
                int sabitIndex = i - DUZENLENEBILIR_AKSAM_SAYISI;
                if (sabitIndex < SABIT_AKSAM_YEMEGI.length) {
                    field.setText(SABIT_AKSAM_YEMEGI[sabitIndex]);
                }
            }
        }
    }
}
