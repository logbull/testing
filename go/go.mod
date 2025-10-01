module github.com/logbull/logbull-go-testing

go 1.21

require (
	github.com/joho/godotenv v1.5.1
	github.com/logbull/logbull-go v0.0.0
	github.com/sirupsen/logrus v1.9.3
	go.uber.org/zap v1.27.0
)

require (
	go.uber.org/multierr v1.11.0 // indirect
	golang.org/x/sys v0.18.0 // indirect
)

replace github.com/logbull/logbull-go => ../../libraries/go
