# FinStream Transactions Generator CLI 🏦⚡

A powerful, modular Java CLI tool designed to generate realistic synthetic financial transactions and bank account statements. 

Inspired by **[Synthea™](https://github.com/synthetichealth/synthea)** (the synthetic patient record generator for healthcare FHIR format), **FinStream** simulates realistic **demographic profiles and financial life events** (recurring monthly salary, rent, utilities, OTT subscriptions, day-to-day micro-UPI transactions, POS card payments, ATM cash withdrawals, and savings interest credits).

Currently, the active statement generation target is the **HDFC Bank CSV Statement Schema**. The codebase is built on an **extensible `StatementSchema` & `SchemaRegistry` architecture** so that when additional bank or wallet schemas (SBI, ICICI, Paytm, PayZapp) are defined in the future, they can be plugged in seamlessly.

---

## 🌟 Key Features

- **Active HDFC CSV Statement Schema**:
  - Full support for HDFC Bank CSV statement format:
    `Date,Narration Value Dat,Debit Amount,Credit Amount,Chq/Ref Number,Closing Balance`
- **Extensible Multi-Bank Architecture**:
  - `StatementSchema` strategy interface and `SchemaRegistry` framework ensure future schemas (SBI, Paytm, ICICI, PayZapp) can be registered without modifying the CSV writer or CLI engine.
- **Synthea-style Financial Life Simulation**:
  - **Account Holder Profiles (`PersonProfile`)**: Models synthetic account holder identities (`--name "Ankit Singh"`, `--account-number "50100987654321"`).
  - **Dynamic Output Filename**: Automatically names output files after the person and account number (`<Name>_<AccNo>_HDFC_statement.csv`).
  - **Flexible Quotas**: Generate any requested transaction count (`-n 50`, `-n 200`).
  - **Lifecycle Cadence**: Models monthly salary credit $\rightarrow$ rent payment $\rightarrow$ utility bills $\rightarrow$ micro-UPI spending $\rightarrow$ interest credits.
- **External Configuration (`app.properties`)**:
  - Properties file located at `src/main/resources/app.properties`. CLI options override properties dynamically.

---

## 🛠️ Architecture Overview

```
src/main/
├── java/dev/byankit/
│   ├── App.java                              # Main CLI entrypoint
│   ├── cli/
│   │   └── GenerateCommand.java              # Picocli command & flag options
│   ├── config/
│   │   └── AppConfig.java                    # app.properties config loader
│   ├── enums/
│   │   ├── TransactionSource.java            # Bank / Source enum
│   │   ├── TransactionType.java              # Statement type enum
│   │   └── TransactionCategory.java          # UPI, NEFT, IMPS, ATM, CARD, SALARY, BILL
│   ├── model/
│   │   ├── PersonProfile.java                # Demographic profile & salary rules
│   │   ├── Transaction.java                  # Core domain transaction model
│   │   └── StatementConfig.java              # Configuration builder
│   ├── schema/
│   │   ├── StatementSchema.java              # Extensible schema interface
│   │   ├── AbstractStatementSchema.java      # Base formatter utilities
│   │   ├── HdfcStatementSchema.java          # HDFC CSV statement schema
│   │   └── SchemaRegistry.java               # Central registry for bank schemas
│   ├── generator/
│   │   ├── DataRandomizer.java               # Randomizer for names, UTRs, amounts
│   │   ├── SyntheaFinancialEngine.java       # Synthea financial lifecycle engine
│   │   ├── StatementGenerator.java           # Strategy interface
│   │   ├── GenericStatementGenerator.java    # Generic schema-backed generator
│   │   └── HdfcStatementGenerator.java       # HDFC generator implementation
│   ├── factory/
│   │   └── StatementGeneratorFactory.java    # Factory resolving generators
│   └── writer/
│       └── CsvStatementWriter.java           # Universal CSV writer using StatementSchema
└── resources/
    └── app.properties                        # Application properties file
```

---

## ⚙️ Configuration via `app.properties`

```properties
# Default Generator Settings
generator.default.source=HDFC
generator.default.type=BANKSTATEMENT
generator.default.count=50
generator.default.initial_balance=50000.00
generator.default.output_path=hdfc_statement.csv
generator.default.date_format=dd/MM/yy

# Person Demographics & Salary Range
profile.default.min_salary=45000.00
profile.default.max_salary=180000.00
profile.default.rent_percentage=0.25
```

---

## 🚀 Quick Start & Usage

### Build Executable Jar
```bash
mvn clean package
```

### Run Generator
```bash
java -jar target/finstream-transactions-generator-1.0-SNAPSHOT.jar \
  --name "Ankit Kumar Singh" \
  --account-number "50100987654321" \
  -n 50 \
  -b 75000
```
*Generated file*: `ANKIT_KUMAR_SINGH_50100987654321_HDFC_statement.csv`

---

## 📊 Sample Output (`HDFC` CSV Format)

```csv
Date,Narration Value Dat,Debit Amount,Credit Amount,Chq/Ref Number,Closing Balance
22/07/26,NEFT DR-840115951616-STARBUCKS COFFEE,2185.19,,840115951616,72814.81
22/07/26,UPI-900457039117-ELI RIPPIN-SBI-elirippin@upi,1401.55,,900457039117,71413.26
25/07/26,NEFT DR-691233-MAKEMYTRIP,40187.73,,691233,31225.53
27/07/26,UPI-063412-ZEPTO-PAYTM-zepto@paytm,1879.67,,063412,29345.86
30/07/26,ACH C- DURGAN-FRAMI SALARY 744650,,214348.36,744650396268,235043.66
09/08/26,NEFT DR-383575-OLA CABS,19375.39,,383575,215668.27
10/08/26,IMPS-270244-SHELLEY STARK-KOTAK,37084.97,,270244,177961.68
```

---

## 🧪 Running Tests

```bash
mvn clean test
```

---

## 📄 License
MIT License.
