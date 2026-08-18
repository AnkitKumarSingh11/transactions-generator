# FinStream Transactions Generator CLI 🏦⚡

A powerful, modular Java CLI tool designed to generate realistic synthetic financial bank transaction statements and user profile metadata.

Inspired by **[Synthea™](https://github.com/synthetichealth/synthea)** (the synthetic patient record generator for healthcare FHIR format), **FinStream** simulates realistic **demographic user profiles and financial life events** (recurring monthly salary, rent, utilities, day-to-day micro-UPI transactions, POS card payments, ATM cash withdrawals, and savings interest credits).

---

## 🌟 Key Features

- **HDFC Bank Statement Schema (Default)**:
  - Header: `Date,Narration Value Dat,Debit Amount,Credit Amount,Chq/Ref Number,Closing Balance`
- **User Profile Metadata CSV Generation**:
  - Automatically outputs a companion User Details CSV alongside every statement containing demographic & bank account details.
  - Header: `Name, Email, Mobile Number, Account Number, IFSC Code, UPI ID, Customer ID, Company Name, Monthly Salary`
- **Extensible Multi-Bank Architecture**:
  - `StatementSchema` strategy interface and `SchemaRegistry` framework support bank transaction schemas (HDFC, SBI).
- **Auto-Generated Filenames with `-o /path/to/dir`**:
  - The `-o` / `--output` flag expects a target directory or output path.
  - Filenames are automatically generated inside the directory based on the person's name and Account Number:
    - Statement CSV: `<Name>_<AccountNo>_HDFC_statement.csv`
    - User Details CSV: `<Name>_<AccountNo>_user_details.csv`
- **Synthea-style Financial Life Simulation**:
  - Models demographic profiles (`PersonProfile`), customizable account holder names (`--name "Ankit Singh"`), account numbers (`--account-number "50100987654321"`), and realistic salary/expense flows.

---

## 🚀 Quick Start & Usage

### Build Executable Jar
```bash
mvn clean package
```

### Run Generator (Default: HDFC Bank Statement + User Details CSV)
```bash
java -jar target/finstream-transactions-generator-1.0.1-SNAPSHOT.jar \
  --name "Ankit Kumar Singh" \
  --account-number "50100987654321" \
  -n 50 \
  -b 75000 \
  -o ./output_dir/
```

*Generated Files*:
- **Bank Statement**: `./output_dir/ANKIT_KUMAR_SINGH_50100987654321_HDFC_statement.csv`
- **User Details**: `./output_dir/ANKIT_KUMAR_SINGH_50100987654321_user_details.csv`

---

## ⚙️ CLI Options

```
Usage: generate [-hV] [-b=<initialBalance>] [-c=<configPath>]
                [--date-format=<dateFormat>] [--end-date=<endDateStr>]
                [-n=<count>] [--name=<accountHolderName>]
                [--account-number=<accountNumber>]
                [-o=<outputPath>] [-s=<sourceName>] [--start-date=<startDateStr>]
                [-t=<typeName>]

Options:
  -o, --output=<outputPath>          Target output directory (or file path). Default: current directory
  -s, --source=<sourceName>          Source bank schema (HDFC, SBI, ALL). Default: HDFC
      --name, --account-holder=<name>  Custom account holder name.
      --account-number, --account-no   Custom account number.
  -n, --count=<count>                Number of transactions to generate per statement.
  -b, --initial-balance=<balance>    Starting account balance.
  -h, --help                         Show help message.
```

---

## 📄 User Details CSV Format Example

```csv
Name,Email,Mobile Number,Account Number,IFSC Code,UPI ID,Customer ID,Company Name,Monthly Salary
ANKIT KUMAR SINGH,ankitkumarsingh542@example.com,+919876543210,50100987654321,HDFC0001234,ankitkumarsingh@okhdfcbank,CUST0987654,GOOGLE INDIA,125000.00
```

---

## 🧪 Running Tests

```bash
mvn clean test
```

---

## 📄 License
MIT License.
