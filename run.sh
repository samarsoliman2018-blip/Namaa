#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║              NAMAA SMART WAQF PLATFORM                     ║${NC}"
echo -e "${BLUE}║                   Starting Up...                          ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"

# Find Java
JAVA_CMD=""
if [ -n "$JAVA_HOME" ] && [ -f "$JAVA_HOME/bin/java" ]; then
    JAVA_CMD="$JAVA_HOME/bin/java"
elif command -v java &> /dev/null; then
    JAVA_CMD="java"
else
    echo -e "${RED}❌ Java not found! Please install Java 11 or higher.${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Java found: $($JAVA_CMD -version 2>&1 | head -n 1)${NC}"

# Compile all Java files
echo -e "${BLUE}📦 Compiling Namaa application...${NC}"

# Create bin directory if it doesn't exist
mkdir -p bin

# Compile all Java files
$JAVA_CMD -d bin -cp "src/main/java" $(find src/main/java -name "*.java")

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Compilation successful!${NC}"
else
    echo -e "${RED}❌ Compilation failed!${NC}"
    exit 1
fi

# Copy CSV files to bin directory for access
echo -e "${BLUE}📂 Copying data files...${NC}"
cp -f *.csv bin/ 2>/dev/null || echo -e "${RED}⚠️ No CSV files found in root directory.${NC}"

# Run the application
echo -e "${BLUE}🚀 Starting Namaa application...${NC}"
echo -e "${GREEN}════════════════════════════════════════════════════════════════${NC}"

cd bin
$JAVA_CMD Main.Main

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Application closed successfully.${NC}"
else
    echo -e "${RED}❌ Application encountered an error.${NC}"
fi
