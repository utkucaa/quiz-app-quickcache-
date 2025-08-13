# QuizApp QuickCache

JWT tabanlı kimlik doğrulama ve Redis cache desteğiyle güçlendirilmiş bir **Quiz Uygulaması**dır.  
Kullanıcılar kayıt olabilir, giriş yapabilir, quiz çözebilir ve skor tablosunda sıralamalarını görebilir.  
Redis sayesinde quiz ve skor verileri **milisaniyeler içinde** yüklenir.


## Öne Çıkan Özellikler
- **JWT Authentication** ile güvenli giriş/kayıt sistemi
- **Redis Cache** ile yüksek performanslı veri getirimi
- **Leaderboard** (skor tablosu) sistemi
- **Katmanlı Mimari** (Controller → Service → Repository)
- **DTO ve Entity Ayrımı**
- **Custom Exception Handling**
- **MySQL**
- 

<img width="873" height="701" alt="Image" src="https://github.com/user-attachments/assets/25527e8f-60be-4373-9f9d-622215bb57d3" />



## ⚡ Redis Cache Kullanımı

Redis, bu projede **quiz** ve **leaderboard** verilerini hızlıca getirmek için kullanıldı.

### Çalışma Mantığı:
1. İlk sorguda veri **DB**’den alınır ve **Redis cache**’e eklenir.
2. Sonraki sorgularda veri **Redis**’ten okunur (milisaniye sürede).
3. Cache, **TTL (Time To Live)** süresi ile otomatik olarak temizlenir.
4. 

<img width="823" height="362" alt="Image" src="https://github.com/user-attachments/assets/d049bf3d-ae53-483b-9156-3932ad2b02b3" />
