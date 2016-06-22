#!/bin/bash

jar -uvf target/spring-boot-gemfire-server-0.0.1-SNAPSHOT.jar com/gemstone/gemfire/management/internal/cli/commands/
jar -uvf target/spring-boot-gemfire-server-0.0.1-SNAPSHOT.jar com/gemstone/gemfire/management/internal/cli/converters/
jar -uvf target/spring-boot-gemfire-server-0.0.1-SNAPSHOT.jar org/springframework/shell/converters/