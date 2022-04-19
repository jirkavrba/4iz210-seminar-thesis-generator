# Generátor semestrálek z předmětu 4IZ210

![page](https://user-images.githubusercontent.com/14146321/164044407-25ee91ac-844a-46e6-8b8b-637abe69414c.png)

# Jak generátor nainstalovat a spustit?

- ## Vím co je docker a umím s ním pracovat
  - Image je v docker hubu jako `jirkavrba/4iz210-seminar-thesis-generator`
  - Je potřeba namapovat port 8080 pro správné fungování, nebo nastavit env proměnnou `PORT`

- ## Nevím co je docker
  - [Docker in 100 seconds](https://www.youtube.com/watch?v=Gjnup-PuquQ)
  - Je potřeba zprovoznit si Docker, nejjednodušší způsob je stáhnout si [Docker Desktop](https://www.docker.com/products/docker-desktop/)
  - V powershell spustit příkaz `docker run -d -p 8080:8080 --name=4iz210 jirkavrba/4iz210-seminar-thesis-generator`
    ![powershell](https://user-images.githubusercontent.com/14146321/164059867-31fd9233-c615-49d1-8705-747560d58d1b.png)
  - po stažení a spuštění se container objeví v Docker Desktop rozhraní
    ![docker desktop](https://user-images.githubusercontent.com/14146321/164060188-5fe8aa83-9723-49bd-90e0-28bb84f7c1de.png)
  - Aplikace je po spuštění docker containeru dostupná na [http://localhost:8080](http://localhost:8080)
  - Pro vypnutí aplikace stačí kliknout na **STOP**, případně **DELETE** tlačítko v docker desktop rozhraní
    ![docker desktop stop](https://user-images.githubusercontent.com/14146321/164060382-9b133597-131d-4b42-86f0-4829d92c2c75.png)
