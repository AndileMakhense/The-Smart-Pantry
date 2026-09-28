
STUDENT NAME: Andile Makhense
ITS Number: 402312724
Year: 3rd
Qua: Bsc IT

# Smart Pantry Manager

Smart Pantry Manager is a Java Android application that reduces food waste by tracking leftover ingredients and suggesting only recipes for which every required ingredient and quantity is already available.

## Core features

- Create, read, update and delete pantry ingredients.
- Persist pantry data locally with SQLite through `SQLiteOpenHelper`.
- Browse strict recipe suggestions calculated from 18 recipes seeded on first run.
- Open a full recipe ingredient list and preparation method.
- Save expiry-alert and measurement preferences.
- Handle empty lists and invalid form input clearly.
- Navigate between Pantry, Suggested Recipes and Settings.

## Why SQLite

SQLite was selected because the application is pantry-focused, works fully offline, requires no account or server, and stores a small relational dataset. It also makes persistence and CRUD behaviour easy to demonstrate and inspect. The schema uses `pantry_items`, `recipes`, and `recipe_ingredients` tables.

## Strict-matching rule

`RecipeMatcher` normalises simple plural ingredient names, combines duplicate pantry entries, converts compatible metric units such as kilograms to grams and litres to millilitres, and then checks every required ingredient. A recipe is returned only when every requirement is present in sufficient quantity.

## Requirements

- Android Studio Ladybug or newer
- Android SDK 35
- JDK 17
- Android device or emulator running Android 7.0 API 24 or newer

## Run instructions

1. Open Android Studio and choose **Open**.
2. Select the `SmartPantryManager` folder.
3. Allow Gradle sync to finish. If Android Studio asks, install Android SDK 35.
4. Create or select an emulator with API 24 or newer.
5. Click **Run app**.

## Demonstration data

For a quick proof of strict matching, add these pantry items:

| Ingredient | Quantity | Unit |
| --- | ---: | --- |
| Eggs | 2 | item |
| Tomato | 1 | item |
| Oil | 1 | tbsp |
| Salt | 1 | tsp |

`Tomato Omelette` will appear. Delete or reduce any one of these items and it will disappear from Suggested Recipes.

For a metric-conversion demonstration, enter `Pasta` as `0.5 kg`; the matcher treats this as `500 g`.

## Project structure

- `data/DatabaseHelper.java` - schema, CRUD queries, and recipe seeding.
- `service/RecipeMatcher.java` - strict matching business logic.
- `util/IngredientNormalizer.java` - name and unit normalisation.
- `ui/` - Activities, navigation helper, and custom RecyclerView adapters.
- `model/` - pantry and recipe data classes.
- `app/src/test/` - unit tests for matching edge cases.

## Running tests

In Android Studio, right-click `RecipeMatcherTest` and choose **Run**. The tests verify missing-ingredient exclusion, plural normalisation, metric conversion, and duplicate-entry aggregation.

## GitHub setup

Create a public repository, then connect and push this existing history:

```bash
git branch -M main
git remote add origin https://github.com/AndileMakhense/The-Smart-Pantry.git
git push -u origin main
```

## Privacy and permissions

The app requests no internet, location, contacts, camera, or storage permission. All user pantry data remains in the local SQLite database.
