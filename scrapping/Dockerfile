FROM python:3.11-slim

# Instalar dependencias del sistema necesarias para Chrome
RUN apt-get update && apt-get install -y \
    wget \
    curl \
    gnupg \
    unzip \
    fonts-liberation \
    libnss3 \
    libxss1 \
    libappindicator3-1 \
    libasound2 \
    libatk-bridge2.0-0 \
    libgtk-3-0 \
    libgbm-dev \
    libxshmfence-dev \
    xdg-utils \
    && rm -rf /var/lib/apt/lists/*

# Añadir la clave de Google y repositorio de Chrome
RUN mkdir -p /etc/apt/keyrings && \
    curl -fsSL https://dl.google.com/linux/linux_signing_key.pub | gpg --dearmor -o /etc/apt/keyrings/google.gpg && \
    echo "deb [arch=amd64 signed-by=/etc/apt/keyrings/google.gpg] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google.list && \
    apt-get update && \
    apt-get install -y google-chrome-stable && \
    rm -rf /var/lib/apt/lists/*

# Copiar dependencias Python
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copiar tu script
COPY scrap.py .

# Ejecutar y dejar contenedor en ejecución
CMD true && tail -f /dev/null
