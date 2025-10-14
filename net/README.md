# LogBull .NET Integration Tests

This directory contains integration tests for the LogBull .NET library that demonstrate all three integration options by sending actual logs to a running LogBull server.

## Prerequisites

- .NET 6.0 SDK or higher
- A running LogBull server instance
- LogBull project ID and host URL

## Configuration

### Option 1: Using Environment Variables

Set the following environment variables:

```bash
export LOGBULL_PROJECT_ID="your-project-id-here"
export LOGBULL_HOST="http://localhost:4005"
export LOGBULL_API_KEY="your-api-key-here"  # optional
```

On Windows (PowerShell):

```powershell
$env:LOGBULL_PROJECT_ID="your-project-id-here"
$env:LOGBULL_HOST="http://localhost:4005"
$env:LOGBULL_API_KEY="your-api-key-here"  # optional
```

### Option 2: Using .env File

Create a `.env` file in the `testing/` directory (one level up from this folder):

```env
LOGBULL_PROJECT_ID=your-project-id-here
LOGBULL_HOST=http://localhost:4005
LOGBULL_API_KEY=your-api-key-here
```

## Building and Running

### Using dotnet CLI

1. **Restore dependencies:**

```bash
dotnet restore
```

2. **Build the project:**

```bash
dotnet build
```

3. **Run the tests:**

```bash
dotnet run
```

### Using Makefile

From the `testing/` directory:

```bash
# Install dependencies
make install

# Run all tests (including .NET)
make test          # Linux/Mac
make test-windows  # Windows
```

## What the Tests Do

The integration tests demonstrate three LogBull .NET integration options:

### 1. Standalone LogBull Logger

- Basic logging with all log levels (DEBUG, INFO, WARNING, ERROR, CRITICAL)
- Structured logging with custom fields
- Context management with `WithContext()`
- Nested context chaining

### 2. Microsoft.Extensions.Logging Integration

- Integration with MEL logger factory
- Structured logging with placeholders
- Using scopes for contextual logging
- Category-based loggers

### 3. Serilog Integration

- Serilog sink configuration
- Property-based structured logging
- Log context enrichment with `LogContext`
- Structured object logging with `@` destructuring
- Exception logging

## Expected Output

When you run the tests, you should see:

```
LogBull .NET Library Integration Tests
========================================
Loaded configuration from ../.env
Configuration loaded:
  Project ID: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
  Host: http://localhost:4005
  API Key: ***

=== LogBull Logger Demo ===
LogBull Logger demo completed

=== Microsoft.Extensions.Logging Demo ===
Microsoft.Extensions.Logging demo completed

=== Serilog Integration Demo ===
Serilog Integration demo completed

=== Demo Complete ===
Check your LogBull server for the logged messages!
Waiting for 1 seconds...
Waiting for 2 seconds...
Waiting for 3 seconds...
Waiting for 4 seconds...
Waiting for 5 seconds...
```

## Verifying Results

After running the tests:

1. Open your LogBull server dashboard
2. Navigate to your project
3. Look for log entries with prefixes:
   - `[net - logbull_logger - N]` - From standalone logger
   - `[net - mel - N]` - From Microsoft.Extensions.Logging
   - `[net - serilog - N]` - From Serilog

Each entry should contain:

- The appropriate log level
- The message with the prefix
- Structured fields/properties specific to that log entry

## Troubleshooting

### "LOGBULL_PROJECT_ID environment variable is required"

Make sure you've set the environment variables or created the `.env` file as described in the Configuration section.

### "LogBull: server returned status 401"

Your API key might be incorrect or missing. Check your configuration.

### "LogBull: HTTP request failed"

- Verify that your LogBull server is running
- Check that the host URL is correct and accessible
- Ensure there are no firewall or network issues

### No logs appearing in LogBull

- Wait a few seconds for logs to be sent (the program waits 5 seconds)
- Check the console output for any error messages
- Verify the project ID matches your LogBull project

## Project Structure

```
testing/net/
├── LogBullTest.csproj   # Project configuration
├── Program.cs            # Main test program with all demos
├── README.md            # This file
└── .gitignore          # Git ignore rules
```

## Related Documentation

- [LogBull .NET Library README](../../libraries/net/README.md)
- [LogBull Server Documentation](https://github.com/logbull/logbull)
