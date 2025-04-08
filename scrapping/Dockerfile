# Usar una imagen base de Python
FROM python:3.13-slim

# Establecer el directorio de trabajo
WORKDIR /scrapping

# Instalar dependencias del sistema operativo necesarias para Chrome y Selenium
RUN apt-get update && apt-get install -y \
    wget \
    curl \
    unzip \
    libxss1 \
    libappindicator3-1 \
    libindicator7 \
    libgdk-pixbuf2.0-0 \
    libnspr4 \
    libnss3 \
    libatk-bridge2.0-0 \
    libxcomposite1 \
    libxrandr2 \
    libgdk-pixbuf2.0-0 \
    xvfb \
    fonts-liberation \
    libappindicator3-1 \
    libasound2 \
    libgconf-2-4 \
    libnss3 \
    libx11-xcb1 \
    && rm -rf /var/lib/apt/lists/*

# Instalar Google Chrome (si no está instalado previamente)
RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
RUN dpkg -i google-chrome-stable_current_amd64.deb || apt-get install -fy

# Instalar Selenium y webdriver-manager
RUN pip install --upgrade pip
RUN pip install selenium webdriver-manager

# Descargar chromedriver usando webdriver-manager
RUN python -m webdriver_manager.chrome

# Copiar tu código Python al contenedor
COPY . /scrapping

# Ejecutar el script de scraping
CMD ["python", "scrap.py"]
