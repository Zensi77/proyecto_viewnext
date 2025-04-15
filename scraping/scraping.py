import json
import time
import mysql
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import StaleElementReferenceException
from webdriver_manager.chrome import ChromeDriverManager
import os

# ---------- Configuración Global ----------
def setup_driver():
    options = Options()
    options.add_argument('--headless')
    options.add_argument('--no-sandbox')
    options.add_argument('--disable-dev-shm-usage')
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service, options=options)
    driver.set_page_load_timeout(60)
    return driver

def load_config(path='properties.json'):
    with open(path, 'r') as f:
        return json.load(f)

def saveOnDatabase(data):
    try:
        conn = mysql.connector.connect({
            "host": "localhost",
            "user": "root",
            "password": "root",
            "database": "tienda-vn"
        })

        if not conn.is_connected():
            print("[ERROR] No se pudo conectar a la base de datos.")
            return
        
        cursor = conn.cursor()

        sql = '''
            INSERT INTO articulos (provider, nombre, precio, imagen, enlace)
            VALUES (%s, %s, %s, %s, %s, %s)
        '''

        precio = data.get("precio").concat(".", data.get("decimal")) if data.get("decimal") else data.get("precio")
        if precio:
            precio = precio.replace(",", ".")
        else:
            precio = 0
        
        values = (
            data.get("provider"),
            data.get("nombre"),
            data.get("imagen"),
            data.get("enlace")
        )

        cursor.execute(sql, values)
        conn.commit()

        print(f"Guardado en la base de datos: {data.get('nombre')}")

    except mysql.connector.Error as err:
        print("Error al guardar en la base de datos:", err)

    finally:
        if conn.is_connected():
            cursor.close()
            conn.close()

def accept_cookies(driver, font):
    try:
        selectors = {
            "pcbox": 'div#cookiescript_accept',
            "coolmod": 'button#CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll'
        }
        if font in selectors:
            cookie_btn = WebDriverWait(driver, 15).until(
                EC.element_to_be_clickable((By.CSS_SELECTOR, selectors[font]))
            )
            cookie_btn.click()
            print("[Info] Cookies aceptadas")
    except Exception as e:
        print(f"[Warn] No se pudieron aceptar cookies o ya estaban aceptadas en {font}")

def interact_with_search_input(driver, selectors, font, search_term):
    try:
        search_input = WebDriverWait(driver, 20).until(
            EC.presence_of_element_located((By.CSS_SELECTOR, selectors[font]))
        )
        search_input.clear()
        search_input.send_keys(search_term)
        if font == "pcbox": # Ya que en esta web el boton es un formulario
            search_input.submit()
        WebDriverWait(driver, 20).until(
            EC.presence_of_element_located((By.TAG_NAME, 'body'))
        )
    except StaleElementReferenceException:
        print("[Retry] Search input obsoleto. Reintentando...")
        interact_with_search_input(driver, selectors, font, search_term)

def extract_product_data(product, selectors, font, current_url):
    result = {
        "provider": 1 if font == "pcbox" else 2
    }
    for key, sel in selectors.items():
        try:
            if sel[font]:
                if key == "imagen":
                    result[key] = product.find_element(By.CSS_SELECTOR, sel[font]).get_attribute('src')
                elif key == "enlace":
                    result[key] = current_url
                else:
                    result[key] = product.find_element(By.CSS_SELECTOR, sel[font]).text
            else:
                result[key] = None
        except Exception as e:
            print(f"[Error] Al extraer '{key}': {e}")
            result[key] = None
    return result

def scrape_font(driver, data, font):
    print(f"[Inicio] Scrapeando {font}")
    for _, search_term in data['searchterms'].items():
        try:
            driver.get(data['fonts'][font])
            time.sleep(3)
            accept_cookies(driver, font)
            interact_with_search_input(driver, data['inputSearch'], font, search_term)

            WebDriverWait(driver, 20).until(
                EC.presence_of_all_elements_located((By.CSS_SELECTOR, data['cardProduct'][font]))
            )
            products = driver.find_elements(By.CSS_SELECTOR, data['cardProduct'][font])
            print(f"[OK] {len(products)} productos encontrados para '{search_term}' en {font}")

            for product in products:
                item_data = extract_product_data(product, data['data'], font, driver.current_url)
                # saveOnDatabase(item_data)
                print(f"[Info] Extraído: {item_data}")

        except Exception as e:
            print(f"[Error] En búsqueda '{search_term}' en {font}: {e}")
            os.makedirs('/scraping/errors', exist_ok=True)
            filename = f"/scraping/errors/{font}_{search_term.replace(' ', '_')}.png"
            driver.save_screenshot(filename)
            print(f"[Captura] Guardada en {filename}")

# ---------- Ejecución principal ----------
if __name__ == '__main__':
    print("[WARNING] Se borrarán todos los datos de la base de datos, desea continuar? (s/n)")
    respuesta = input().strip().lower()
    if respuesta != 's':
        print("[FIN] Proceso cancelado.")
        exit(0)

    data = load_config()
    driver = setup_driver()
    driver.get("https://www.google.com")  # Carga inicial para evitar errores de WebDriver

    for font in data['fonts']:
        scrape_font(driver, data, font)

    driver.quit()
    print("[FIN] Scraping completado.")
