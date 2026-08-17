# Release Guide for FinStream Transactions Generator 🚀

This document outlines the step-by-step procedure to release version `v1.0.0` of **FinStream Transactions Generator**.

---

## 📋 Release Checklist for `v1.0.0`

### 1. Verify Local Build & Test Suite
Ensure all automated unit tests pass locally:
```bash
mvn clean test
```

Build the final executable fat JAR:
```bash
mvn clean package
```
Verify that `target/finstream-transactions-generator-1.0.0.jar` is successfully produced.

---

### 2. Commit All Changes to Git
```bash
git add .
git commit -m "release: v1.0.0 - Initial release with HDFC & Paytm statement schemas"
```

---

### 3. Create & Push Git Tag
Create an annotated tag for version `v1.0.0`:
```bash
git tag -a v1.0.0 -m "FinStream Transactions Generator v1.0.0 Initial Release"
```

Push commits and the tag to GitHub:
```bash
git push origin main
git push origin v1.0.0
```

---

### 4. Automated GitHub Release Publishing

Once the `v1.0.0` tag is pushed to GitHub, the **GitHub Actions Release Workflow** ([`.github/workflows/release.yml`](./.github/workflows/release.yml)) will automatically:
1. Compile and package the Java application executable JAR.
2. Publish a official **GitHub Release for `v1.0.0`**.
3. Attach the compiled executable `finstream-transactions-generator.jar` as a release asset available for direct download!

---

## 🌟 What's Included in Release `v1.0.0`

- **Synthea-style Financial Life Simulation Engine**: Models demographic profiles (`PersonProfile`) and realistic financial event cadences (monthly salary, rent, utility bills, micro-transactions, and interest credits).
- **HDFC Bank CSV Statement Schema**: Full support for HDFC Bank CSV statement format:
  `Date,Narration Value Dat,Debit Amount,Credit Amount,Chq/Ref Number,Closing Balance`
- **Paytm Wallet CSV Statement Schema**: Full support for Paytm statement export format:
  `Date,Time,Transaction Details,Other Transaction Details (UPI ID or A/c No),Your Account,Amount,UPI Ref No.,Order ID,Remarks,Tags,Comment`
- **Universal Strategy Architecture**: Modular `StatementSchema` and `SchemaRegistry` framework allowing future bank/wallet statement formats to be added effortlessly.
- **Dynamic File Naming**: Automatically names output files after account holder names & account numbers or Paytm VPAs (e.g., `ANKIT_SINGH_50100987654321_HDFC_statement.csv`).
- **External Properties Configuration**: `app.properties` default loading with dynamic CLI flag overrides.
