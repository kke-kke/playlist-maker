# 📌 Feature: Избранные треки

## 📝 Test Case ID: TC-005
## 🎯 Title: Добавление трека в избранное

**Preconditions:**
- Трек не находится в избранном

### 🔹 Steps:

1. Нажать на иконку "сердце"

### ✅ Expected result:

- Трек добавлен в избранное
- Иконка становится активной (drawable сменяется на favorite_filled.xml)


## 📝 Test Case ID: TC-006
## 🎯 Title: Удаление трека из избранного

**Preconditions:**
- Трек находится в избранном

### 🔹 Steps:

1. Нажать на иконку "сердце"

### ✅ Expected result:

- Трек удалён из избранного
- Иконка становится неактивной (drawable сменяется на favorite_border.xml)
