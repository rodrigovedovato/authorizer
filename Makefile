build:
	docker build -f builder --output type=tar,dest=authorizer.tar .