# FinStream Transactions Generator CLI 🏦⚡

A powerful, modular Java CLI tool designed to generate realistic synthetic financial transactions and bank account statements across different schemas. 

Inspired by **[Synthea™](https://github.com/synthetichealth/synthea)** (the synthetic patient record generator for healthcare FHIR format), **FinStream** simulates realistic **demographic profiles and financial life events** (recurring monthly salary, rent, utilities, OTT subscriptions, day-to-day micro-UPI transactions, POS card payments, ATM cash withdrawals, and savings interest credits).

---

## 🌟 Key Features

- **Synthea-style Financial Life Simulation**:
  - **Account Holder Profiles (`PersonProfile`)**: Generates realistic Indian synthetic identities with demographic details, company affiliations, income levels, and UPI handles.
  - **Customizable Name & Account Number**: Specify custom account holder names (`--name "Ankit Singh"`) and account numbers (`--account-number "50100987654321"`).
  - **Dynamic Output Filename**: Automatically names output files after the person and account number (e.g. `ANKIT_SINGH_50100987654321_HDFC_statement.csv`), allowing instant identification of statement ownership.
  - **Arbitrary Transaction Counts**: Easily specify the exact number of transactions to generate (`-n 100`, `-n 500`).
  - **Lifecycle Modules**: Simulates real-world financial cadence—Monthly Salary Credit $\rightarrow$ Rent Payment $\rightarrow$ Utility & Broadband Bills $\rightarrow$ Daily Groceries (Blinkit/Zepto/D-Mart) $\rightarrow$ Food Delivery (Swiggy/Zomato) $\rightarrow$ P2P UPI transfers $\rightarrow$ Quarterly Savings Bank Interest.
- **HDFC CSV Statement Schema**:
  - Full support for HDFC Bank CSV statement exports:
    `Date,Narration Value Dat,Debit Amount,Credit Amount,Chq/Ref Number,Closing Balance`
- **External Configuration (`app.properties`)**:
  - Centralized configuration file located in `src/main/resources/app.properties`.
  - Configurable transaction defaults, demographic salary boundaries, and lifecycle probabilities.
  - Command Line (CLI) arguments override `app.properties` seamlessly.
- **Extensible Architecture**:
  - Clean separation using **Strategy Pattern** (`StatementGenerator`) and **Factory Pattern** (`StatementGeneratorFactory`), making it trivial to add support for new bank formats (SBI, Paytm, ICICI, etc.).
- **Accurate Running Balance Math**:
  - Chronological transaction ordering and precise balance calculation (`closing_balance = prev_balance + credit - debit`).

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
│   │   ├── TransactionSource.java            # HDFC, SBI, PAYTM, PAYZAPP
│   │   ├── TransactionType.java              # BANKSTATEMENT, UPISERVICE, WALLET
│   │   └── TransactionCategory.java          # UPI, NEFT, IMPS, ATM, CARD, SALARY, BILL
│   ├── model/
│   │   ├── PersonProfile.java                # Demographic profile & salary rules
│   │   ├── Transaction.java                  # Core domain transaction model
│   │   └── StatementConfig.java              # Configuration builder
│   ├── generator/
│   │   ├── DataRandomizer.java               # Randomizer for names, UTRs, amounts
│   │   ├── SyntheaFinancialEngine.java       # Synthea financial lifecycle simulation engine
│   │   ├── StatementGenerator.java           # Strategy interface
│   │   └── HdfcStatementGenerator.java       # HDFC statement generator strategy
│   ├── factory/
│   │   └── StatementGeneratorFactory.java    # Strategy factory
│   └── writer/
│       └── CsvStatementWriter.java           # HDFC CSV statement writer
└── resources/
    └── app.properties                        # Application properties file
```

---

## ⚙️ Configuration via `app.properties`

The generator loads defaults from `src/main/resources/app.properties`. You can also supply a custom properties file using `-c /path/to/custom.properties`.

```properties
# Default Bank Schema and Transaction Type
generator.default.source=HDFC
generator.default.type=BANKSTATEMENT
generator.default.count=50
generator.default.initial_balance=50000.00
generator.default.output_path=hdfc_statement.csv
generator.default.date_format=dd/MM/yy

# Person Demographics & Financial Profile Defaults
profile.default.min_salary=45000.00
profile.default.max_salary=180000.00
profile.default.rent_percentage=0.25

# Lifecycle Event Frequency Probabilities
lifecycle.prob.upi_daily=0.85
lifecycle.prob.card_shopping=0.40
lifecycle.prob.atm_withdrawal=0.15
lifecycle.prob.utility_bill=0.95
```

---

## 🚀 Quick Start & Usage

### 1. Requirements & Build
- Java 17+
- Maven 3.8+

Build the executable fat JAR:
```bash
mvn clean package
```

### 2. Command Line Execution Examples

Generate statement with auto-named file (`<Name>_<AccNo>_HDFC_statement.csv`):
```bash
java -jar target/finstream-transactions-generator-1.0-SNAPSHOT.jar
```

Generate 150 transactions for a custom name and account number:
```bash
java -jar target/finstream-transactions-generator-1.0-SNAPSHOT.jar \
  --name "Ankit Kumar Singh" \
  --account-number "50100987654321" \
  -n 150 \
  -b 100000 \
  --start-date 2023-01-01 \
  --end-date 2023-06-30
```
*Output File automatically generated*: `ANKIT_KUMAR_SINGH_50100987654321_HDFC_statement.csv`

Explicitly specify an output file path:
```bash
java -jar target/finstream-transactions-generator-1.0-SNAPSHOT.jar \
  --name "Rahul Sharma" \
  -n 75 \
  -o my_custom_statement.csv
```

### 3. CLI Command Options

```
Usage: generate [-hV] [-b=<initialBalance>] [-c=<configPath>]
                [--date-format=<dateFormat>] [--end-date=<endDateStr>]
                [-n=<count>] [--name=<accountHolderName>]
                [--account-number=<accountNumber>] [-o=<outputPath>]
                [-s=<sourceName>] [--start-date=<startDateStr>] [-t=<typeName>]

Options:
      --name, --account-holder=<name>  Custom account holder name (e.g. 'Ankit Singh').
      --account-number, --account-no   Custom account number (e.g. '50100987654321').
  -n, --count=<count>                Number of transactions to generate. Default: 50
  -b, --initial-balance=<balance>    Starting account balance. Default: 50000.00
  -s, --source=<sourceName>          Bank schema (HDFC, SBI, PAYTM, PAYZAPP). Default: HDFC
  -t, --type=<typeName>              Transaction type (BANKSTATEMENT, UPISERVICE, WALLET).
  -o, --output=<outputPath>          Output CSV file path. Default: <Name>_<AccNo>_<Bank>_statement.csv
  -c, --config=<configPath>          Path to custom app.properties file.
      --start-date=<YYYY-MM-DD>      Start date (Default: 30 days ago)
      --end-date=<YYYY-MM-DD>        End date (Default: today)
      --date-format=<dateFormat>     Output CSV date format. Default: dd/MM/yy
  -h, --help                         Show help message and exit.
  -V, --version                      Print version information.
```

---

## 📊 Sample Output (HDFC CSV Format)

Generated file: `ANKIT_KUMAR_SINGH_50100987654321_HDFC_statement.csv`:
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

To run all unit tests:
```bash
mvn clean test
```

Unit tests cover:
- `DataRandomizerTest`: Random data integrity (12-digit UTRs, Indian names, date boundaries).
- `SyntheaFinancialEngineTest`: Financial lifecycle event simulation (monthly salary, rent, utilities, interest).
- `HdfcStatementGeneratorTest`: Chronological sorting and running balance precision.
- `CsvStatementWriterTest`: HDFC header formatting and CSV record validation.
- `GenerateCommandTest`: Dynamic name/account number file naming and CLI flag execution.
- `AppConfigTest`: Property loading from `app.properties`.

---

## 📄 License
MIT License.
