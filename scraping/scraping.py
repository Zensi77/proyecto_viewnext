import json
import time
import uuid
import mysql.connector
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import StaleElementReferenceException
from webdriver_manager.chrome import ChromeDriverManager
from selenium.common.exceptions import TimeoutException
import os

# ---------- Configuración de la DB ----------
try:
    # TODO pasar a variables de entorno
    config = {
            "host": "db-sql",
            "user": "root",
            "password": "root",
            "database": "tienda-vn"
        }
    conn = mysql.connector.connect(**config)

    if not conn.is_connected():
        print("[ERROR] No se pudo conectar a la base de datos.")
        exit(1)
except mysql.connector.Error as err:
    print(f"[ERROR] Error de conexión a la base de datos: {err}")
    exit(1)
    
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
    
def resetData():
    try:
        cursor = conn.cursor()
        cursor.execute("DELETE FROM product")
        cursor.execute("DELETE FROM category")
        cursor.execute("DELETE FROM provider")
        conn.commit()
        print("[INFO] Datos de la base de datos eliminados.")
    except mysql.connector.Error as err:
        print("Error al eliminar datos de la base de datos:", err)
    finally:
        if conn.is_connected():
            cursor.close()
    
def saveCategoriesAndProvider(categories, providers):
    
    try:
        cursor = conn.cursor()
        sql = '''
            INSERT INTO provider (id, name, address)
            VALUES (%s, %s, %s)
        '''
        for provider in providers.values():
            cursor.execute(sql, (uuid.uuid4().bytes, provider.split(".")[1], provider))
            conn.commit()
        cursor = conn.cursor()
        sql = '''
            INSERT INTO category (id, name)
            VALUES (%s, %s)
        '''
        for category in categories.values():
            cursor.execute(sql, (uuid.uuid4().bytes, category))
            conn.commit()
        print("[INFO] Categorías y proveedores guardados.")
    except mysql.connector.Error as err:
        print("Error al guardar en la base de datos:", err)
    finally:
        if conn.is_connected():
            cursor.close()

def saveProduct(data, search_term):
    try: 
        cursor = conn.cursor()

        sql_get_category = '''
            SELECT id FROM category WHERE name = %s
        '''

        cursor.execute(sql_get_category, (search_term,))
        category = cursor.fetchone()

        sql_get_provider = '''
            SELECT id FROM provider WHERE name = %s
        '''

        # Obtener el ID del proveedor
        cursor.execute(sql_get_provider, (data.get("provider"),))
        provider = cursor.fetchone()
        if provider:
            data["provider"] = provider[0]
        else:
            data["provider"] = None

        sql_insert = '''
            INSERT INTO product (id, id_provider, name, image, price, id_category)
            VALUES (%s, %s, %s, %s, %s, %s)
        '''

        # Manejo del precio
        precio_str = data.get("precio")
        if precio_str:
            try:
                precio = precio_str.split("€")[0].strip()
                if data.get("decimal"):
                    precio = f"{precio},{data.get('decimal')}"
                
                # Primero eliminar los puntos de miles
                precio = precio.replace(".", "")
                # Luego reemplazar coma por punto decimal
                precio = precio.replace(",", ".")

                precio = float(precio)
                precio = round(precio, 2)  # Redondear a 2 decimales
            except Exception as e:
                print(f"[Error] Al procesar precio '{precio_str}': {e}")
                precio = "0"
        else:
            precio = "0"

        values = (
            uuid.uuid4().bytes,
            data.get("provider"),
            data.get("nombre"),
            data.get("imagen"),
            precio,
            category[0] if category else None,
        )

        cursor.execute(sql_insert, values)
        conn.commit()

        print(f"Guardado en la base de datos: {data.get('nombre')}")

    except mysql.connector.Error as err:
        print("Error al guardar en la base de datos:", err)
    except Exception as e:
        print(f"[Error] Al guardar producto: {e}")
    finally:
        if conn.is_connected():
            cursor.close()

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

def interact_with_search_input(driver, selectors, font, search_term, retries=3):
    try:
        if font == "coolmod":
            try:
                # Si hay un popup, cerrarlo
                WebDriverWait(driver, 5).until(
                    EC.presence_of_element_located((By.ID, "popupImage"))
                )
                driver.execute_script("""
                    let popup = document.getElementById('popupImage');
                    if (popup) popup.remove();
                """)
                print("[Info] Popup de promoción cerrado.")
            except TimeoutException:
                pass  # No apareció el popup, seguimos

            # Interactuar con el campo de búsqueda
            search_input = WebDriverWait(driver, 15).until(
                EC.element_to_be_clickable((By.CSS_SELECTOR, selectors[font]))
            )
            search_input.click()
            search_input.clear()
            search_input.send_keys(search_term)

            # Ver si cambia el input activo
            try:
                active_input = WebDriverWait(driver, 10).until(
                    # lamda para hacer un wait hasta que el input activo sea diferente al de búsqueda
                    lambda d: d.switch_to.active_element if d.switch_to.active_element != search_input else None
                )
                active_input.clear()
                active_input.send_keys(search_term)
                active_input.submit()
            except TimeoutException:
                pass

        elif font == "pcbox":
            search_input = WebDriverWait(driver, 20).until(
                EC.element_to_be_clickable((By.CSS_SELECTOR, selectors[font]))
            )
            search_input.clear()
            search_input.send_keys(search_term)
            search_input.submit()

        WebDriverWait(driver, 20).until(
            EC.presence_of_all_elements_located((By.CSS_SELECTOR, selectors[font]))
        )

    except StaleElementReferenceException:
        if retries > 0:
            print(f"[Retry] Elemento obsoleto en {font}. Reintentando ({retries})...")
            interact_with_search_input(driver, selectors, font, search_term, retries - 1)
        else:
            print(f"[Error] Demasiados reintentos por elemento obsoleto en {font}.")
    except Exception as e:
        print(f"[Error] Al interactuar con el campo de búsqueda en {font}: {e}")


from selenium.common.exceptions import StaleElementReferenceException

from selenium.common.exceptions import StaleElementReferenceException
import time

def extract_product_data(product, selectors, font, current_url):
    result = {
        "provider": font,
    }
    
    # Obtener los datos usando JavaScript
    if font == "coolmod":
        try:
            # Obtener el driver del elemento padre, ya que el elemento puede no estar visible
            driver = product.parent 
            # Aunque sean funciones, se ejecutan como un script de JS
            script = """
            function getElementData(element, selector) {
                try {
                    const el = element.querySelector(selector);
                    return el ? el.textContent.trim() : null;
                } catch (e) {
                    return null;
                }
            }
            
            function getImageSrc(element, selector) {
                try {
                    const img = element.querySelector(selector);
                    return img ? img.getAttribute('src') : null;
                } catch (e) {
                    return null;
                }
            }
            
            const productEl = arguments[0]; // Elemento del producto
            return {
                nombre: getElementData(productEl, arguments[1]),
                precio: getElementData(productEl, arguments[2]),
                imagen: getImageSrc(productEl, arguments[3])
            };
            """
            
            data = driver.execute_script(
                script, 
                product, 
                selectors['nombre'][font],
                selectors['precio'][font], 
                selectors['imagen'][font]
            )
            
            result.update(data)
            result["enlace"] = current_url
            
            # Obtener el decimal si existe
            if selectors.get("decimal") and selectors["decimal"].get(font):
                decimal_elements = product.find_elements(By.CSS_SELECTOR, selectors["decimal"][font])
                result["decimal"] = decimal_elements[0].text.strip() if decimal_elements else None
                
        except Exception as e:
            print(f"[Error] Al extraer datos con JavaScript: {e}")
    else:
        # Código original para otras fuentes
        for key, sel in selectors.items():
            try:
                if not sel.get(font):
                    result[key] = None
                    continue

                elements = product.find_elements(By.CSS_SELECTOR, sel[font])
                if key == "imagen":
                    result[key] = elements[0].get_attribute("src") if elements else None
                elif key == "enlace":
                    result[key] = current_url
                elif key == "decimal":
                    result[key] = elements[0].text.strip() if elements else None
                else:
                    result[key] = elements[0].text.strip() if elements else None
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
            
            # Esperar a que los resultados se carguen completamente
            time.sleep(5)  # Espera adicional para asegurar carga completa
            
            WebDriverWait(driver, 20).until(
                EC.presence_of_all_elements_located((By.CSS_SELECTOR, data['cardProduct'][font]))
            )
            products = driver.find_elements(By.CSS_SELECTOR, data['cardProduct'][font])
            print(f"[OK] {len(products)} productos encontrados para '{search_term}' en {font}")

            for i, product in enumerate(products):
                try:
                    item_data = extract_product_data(product, data['data'], font, driver.current_url)
                    saveProduct(item_data, search_term)
                    # Pequeña pausa entre productos para evitar sobrecarga
                    if i < len(products) - 1:
                        time.sleep(0.5)
                except Exception as e:
                    print(f"[Error] Al procesar producto {i+1}: {e}")

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

    resetData()

    data = load_config()
    saveCategoriesAndProvider(data['searchterms'], data['fonts'])
    driver = setup_driver()
    driver.get("https://www.google.com")  # Carga inicial para evitar errores de WebDriver

    for font in data['fonts']:
        scrape_font(driver, data, font)

    driver.quit()
    print("[FIN] Scraping completado.")
