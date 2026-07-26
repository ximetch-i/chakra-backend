# Chakra Backend

REST API for agricultural suitability analysis in the Sacred Valley (Cusco, Peru). Combines historical climate data from NASA POWER with a fuzzy logic + AHP scoring model to evaluate whether conditions are favorable for planting specific crops.

## Tech Stack

- Java 21, Spring Boot 3.3.5
- PostgreSQL (via Spring Data JPA)
- Spring WebFlux WebClient (NASA POWER API client)
- ModelMapper, Lombok, Jakarta Bean Validation
- Maven

## Getting Started

### Prerequisites

- Java 21+
- Docker (for PostgreSQL)
- NASA POWER API (no key required)

### Running

```bash
# Start PostgreSQL
docker run -d --name chakra-db -e POSTGRES_DB=chakra_db -e POSTGRES_PASSWORD=postgres -p 5432:5432 postgres:16

# Run the application
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`.

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `chakra_db` | Database name |
| `DB_USERNAME` | `postgres` | Database user |
| `DB_PASSWORD` | `postgres` | Database password |

## API

### POST `/api/v1/evaluations`

Create a crop suitability evaluation.

**Request:**

```json
{
  "district": "URUBAMBA",
  "crop": "PAPA",
  "plantingDate": "2027-03-15"
}
```

**Response (201):**

```json
{
  "success": true,
  "message": "Evaluation created successfully",
  "data": {
    "id": 1,
    "district": "URUBAMBA",
    "crop": "PAPA",
    "plantingDate": "2027-03-15",
    "temperature": 14.0,
    "precipitation": 3.09,
    "soilMoisture": 0.76,
    "elevation": 2870.0,
    "score": 92.46,
    "riskLevel": "FAVORABLE",
    "recommendation": "Condiciones favorables para sembrar papa...",
    "createdAt": "2027-03-15T10:30:00"
  }
}
```

**Districts:** URUBAMBA, OLLANTAYTAMBO, CHINCHERO, CALCA, PISAC, ANTA, LIMATAMBO, URCOS, ANDAHUAYLILLAS, OROPESA

**Crops:** PAPA (Potato), MAIZ (Corn), CAFE (Coffee)

**Risk Levels:** FAVORABLE (>=80), CAUTION (50-79), NOT_RECOMMENDED (<50)

## Mathematical Model

The scoring engine combines **fuzzy logic** (trapezoidal membership functions) with **AHP** (Analytic Hierarchy Process) weights to produce a suitability index in [0, 100].

### Input Variables

```
X = {Temperature, Precipitation, SoilMoisture, Elevation}
```

Each variable is normalized independently to [0, 100] before combining.

### Step 1: Fuzzy Normalization (Trapezoidal Membership)

Each variable is transformed using a trapezoidal membership function:

```
f(x) =
  0                           if x <= a
  100 * (x - a) / (b - a)    if a < x < b
  100                         if b <= x <= c
  100 * (d - x) / (d - c)    if c < x < d
  0                           if x >= d
```

```
aptitude
100 |        ________
    |       /        \
    |      /          \
  0 |_____/            \_____
     a    b     c      d
```

Where `a` = critical lower bound, `b-c` = optimal range, `d` = critical upper bound.

### Step 2: Crop Parameters

Each crop has specific trapezoidal parameters derived from agronomic research:

**Papa (Potato):**

| Variable | a | b | c | d |
|----------|---|---|---|---|
| Temperature (°C) | 5 | 17 | 25 | 30 |
| Soil Moisture (%) | 40 | 60 | 80 | 95 |
| Elevation (m) | 1000 | 1000 | 4200 | 4200 |

Note: For elevation, `a=b` and `c=d`, creating a flat-top plateau (step function). Papa grows anywhere between 1000-4200m with equal aptitude.

**Maiz (Corn):**

| Variable | a | b | c | d |
|----------|---|---|---|---|
| Temperature (°C) | 10 | 15 | 25 | 30 |
| Soil Moisture (%) | 40 | 60 | 80 | 95 |
| Elevation (m) | 1500 | 1500 | 3800 | 3800 |

**Cafe (Coffee):**

| Variable | a | b | c | d |
|----------|---|---|---|---|
| Temperature (°C) | 15 | 18 | 22 | 25 |
| Soil Moisture (%) | 40 | 60 | 80 | 90 |
| Elevation (m) | 800 | 1200 | 2600 | 2600 |

### Step 3: Precipitation Normalization

Precipitation uses a different approach since it depends on crop water requirements:

```
Annual requirement for Papa: 400-1200 mm/year
Monthly equivalent: 33-99 mm/month (annual / 365 * 30)
```

Scoring:
- Below minimum: proportional (P_real / P_min * 100)
- Within range: 100
- Above maximum: penalized (excess reduces score)

### Step 4: AHP Weights

A single AHP pairwise comparison matrix was used for all crops, yielding the following factor importance weights:

| Factor | Weight |
|--------|--------|
| Precipitation | 0.558 |
| Temperature | 0.263 |
| Soil Moisture | 0.122 |
| Elevation | 0.057 |

### Step 5: Final Suitability Index

```
SI = w_T * f(T) + w_P * f(P) + w_SM * f(SM) + w_A * f(A)
```

Classification:
- SI >= 80: **FAVORABLE**
- 50 <= SI < 80: **CAUTION**
- SI < 50: **NOT_RECOMMENDED**

### Step 6: Dynamic Recommendations

The system identifies the **limiting factor** (lowest individual score) and generates a specific recommendation. For example, if temperature scores lowest:

> "La temperatura está fuera del rango óptimo para este cultivo."

## NASA POWER Integration

### Data Source

[NASA POWER API](https://power.larc.nasa.gov/) (Prediction Of Worldwide Energy Resources) provides global climatological data.

**Endpoint:** `https://power.larc.nasa.gov/api/temporal/climatology/point`

**Parameters used:**
| Parameter | Description | Unit |
|-----------|-------------|------|
| `T2M` | Temperature at 2 meters | °C |
| `PRECTOTCORR` | Precipitation (corrected) | mm/day |
| `GWETTOP` | Soil moisture (surface) | fraction [0-1] |

### Temperature Correction

NASA POWER uses a ~50km grid resolution. In mountainous terrain like the Andes, the grid cell average elevation (~3740m for Urubamba) is much higher than the actual town (~2870m). This causes the API to return artificially low temperatures.

**Solution:** Apply the standard atmospheric lapse rate correction:

```
T_corrected = T_nasa + (grid_elevation - real_elevation) * 0.0065
```

Where 0.0065 °C/m is the environmental lapse rate (6.5°C per 1000m).

**Example for Urubamba (March):**
- NASA POWER returns: T = 8.33°C at grid elevation 3740m
- Real elevation: 2870m
- Corrected: 8.33 + (3740 - 2870) * 0.0065 = **13.98°C**

### Elevation Source

Real elevation values are hardcoded per district since NASA POWER's grid resolution is too coarse for the Sacred Valley terrain.

| District | Elevation (m) |
|----------|---------------|
| Urubamba | 2870 |
| Ollantaytambo | 2810 |
| Chinchero | 3762 |
| Calca | 2926 |
| Pisac | 2972 |
| Anta | 3400 |
| Limatambo | 2711 |
| Urcos | 3161 |
| Andahuaylillas | 3163 |
| Oropesa | 3157 |

## Project Structure

```
src/main/java/com/chakra/
├── ChakraApplication.java          # Entry point
├── controller/
│   └── EvaluationController.java   # REST endpoint
├── service/
│   ├── EvaluationService.java      # Service interface
│   ├── AgriculturalModel.java      # Scoring model interface
│   └── impl/
│       ├── EvaluationServiceImpl.java    # Orchestrates the flow
│       └── AgriculturalModelImpl.java    # Fuzzy + AHP scoring engine
├── model/
│   ├── CropParameters.java         # Crop-specific trapezoidal params + AHP weights
│   └── FuzzyMembership.java        # Trapezoidal membership function
├── client/
│   ├── NasaPowerClient.java        # NASA POWER API client
│   └── dto/
│       └── NasaPowerClimatologyResponseDTO.java
├── dto/
│   ├── WeatherDataDTO.java
│   ├── request/
│   │   └── EvaluationRequest.java
│   └── response/
│       └── EvaluationResponse.java
├── entity/
│   └── Evaluation.java             # JPA entity
├── enums/
│   ├── District.java
│   ├── Crop.java
│   └── RiskLevel.java
├── mapper/
│   └── EvaluationMapper.java       # Entity <-> DTO
├── config/                         # CORS, WebClient, JPA, ModelMapper
├── exception/                      # Global exception handler
├── validation/                     # Custom @TodayOrFuture annotation
├── response/                       # Generic ApiResponse wrapper
└── util/
    ├── DistrictCoordinatesResolver.java  # Lat/lon/elevation per district
    └── DateUtils.java
```
