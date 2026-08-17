# Contributing to FinStream Transactions Generator 🚀

Thank you for your interest in contributing to **FinStream Transactions Generator**! We welcome contributions from developers of all skill levels, whether you are fixing a bug, improving documentation, or adding support for new bank and wallet statement schemas (e.g., SBI, ICICI, PayZapp, PhonePe, Axis Bank).

---

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How Can I Contribute?](#how-can-i-contribute)
  - [Adding a New Bank / Wallet Schema](#adding-a-new-bank--wallet-schema)
  - [Reporting Bugs & Requesting Features](#reporting-bugs--requesting-features)
- [Development Setup](#development-setup)
- [Testing & Quality Assurance](#testing--quality-assurance)
- [Pull Request Guidelines](#pull-request-guidelines)

---

## Code of Conduct

We aim to build an inclusive, welcoming community. Please be respectful, constructive, and open to feedback in all discussions, issues, and pull requests.

---

## How Can I Contribute?

### Adding a New Bank / Wallet Schema

One of the best ways to contribute is adding support for a new bank or wallet statement schema (e.g. SBI, ICICI, PayZapp, etc.).

Our codebase uses the **Strategy Pattern** for statement layouts. To add a new bank schema:

1. **Add to `TransactionSource` Enum**:
   Update `dev.byankit.enums.TransactionSource` to register your new bank/wallet enum value.
   ```java
   public enum TransactionSource {
       HDFC("HDFC Bank"),
       PAYTM("Paytm Wallet"),
       SBI("SBI Bank"); // Add your bank here
   }
   ```

2. **Implement `StatementSchema`**:
   Create a new schema class under `dev.byankit.schema` extending `AbstractStatementSchema`.
   Define the exact CSV column headers and column formatting logic:
   ```java
   public class SbiStatementSchema extends AbstractStatementSchema {
       @Override
       public TransactionSource getSource() {
           return TransactionSource.SBI;
       }

       @Override
       public List<String> getHeaders() {
           return List.of("Txn Date", "Value Date", "Description", "Ref No.", "Debit", "Credit", "Balance");
       }

       @Override
       public List<String> formatRecord(Transaction tx, StatementConfig config) {
           // Return row cell values matching getHeaders()
       }

       @Override
       public String getDefaultDateFormat() {
           return "dd-MMM-yyyy";
       }
   }
   ```

3. **Register in `SchemaRegistry`**:
   Add your schema in `dev.byankit.schema.SchemaRegistry` static block:
   ```java
   static {
       register(new HdfcStatementSchema());
       register(new PaytmStatementSchema());
       register(new SbiStatementSchema());
   }
   ```

4. **Add Unit Tests**:
   Create a unit test under `src/test/java/dev/byankit/schema/` to verify CSV header rendering and row formatting.

---

## Development Setup

### Prerequisites
- **Java 17+** (JDK 17 recommended)
- **Maven 3.8+**
- **Git**

### Clone & Build
```bash
git clone https://github.com/your-username/finstream-transactions-generator.git
cd finstream-transactions-generator

# Compile and run test suite
mvn clean test

# Build executable fat JAR
mvn clean package
```

---

## Testing & Quality Assurance

Before submitting a Pull Request, make sure all automated unit tests pass cleanly:

```bash
mvn clean test
```

If you add a new feature or fix a bug, please include corresponding unit tests under `src/test/java/`.

---

## Pull Request Guidelines

1. **Fork the repository** and create a feature branch (`git checkout -b feature/sbi-statement-schema`).
2. Keep commits concise and write clear commit messages (e.g., `feat(schema): Add SBI bank statement CSV schema`).
3. Ensure `mvn clean test` passes with zero failures or errors.
4. Push your branch to GitHub and open a **Pull Request** targeting the `main` branch.
5. Provide a description in your PR explaining the changes made and sample output generated.

Thank you for helping make synthetic financial data generation simple and accessible for everyone! 💳✨
