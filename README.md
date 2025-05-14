# 📱 Barretina APP (Android)

Aplicació Android per a cambrers i cambreres, pensada per gestionar comandes en un bar o restaurant de forma àgil i intuïtiva.

---

## ⚙️ Requisits

- Connexió a un servidor WebSocket actiu
- Accés a la base de dades central `barretina7` (MySQL)

---

## 🚀 Funcionalitats Principals

### 🏁 Pantalla d'inici
- Introdueix la **URL del servidor WebSocket**
- Introdueix el **nom del cambrer**
- Guarda la configuració a `CONFIG.XML` per a futurs inicis

### 🏷️ Etiquetes
- Mostra les **categories** de productes disponibles
- Els tags es carreguen des del servidor

### 🛒 Productes
- En tocar un tag, es mostren els productes corresponents
- En seleccionar un producte, s'afegeix a la comanda activa

### 📋 Comanda
- Mostra la **llista de productes afegits**
- Permet veure quantitats, eliminar productes i consultar el **preu total**

### 🍽️ Taules
- Vista general de totes les taules del restaurant
- Permet entrar a una comanda activa d’una taula o crear-ne una de nova

---

## 🔌 Connexió WebSocket

La comunicació amb el servidor es fa mitjançant WebSocket. 

