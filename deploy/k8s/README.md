# Kubernetes local

This folder contains a local-ready setup for:

- app
- Redis
- OpenTelemetry Collector
- Jaeger

## Expected image

Build the app image locally first:

```bash
docker build -t camel-country-summary:local .
```

## Apply manifests

```bash
kubectl apply -k deploy/k8s
```

## Access the API

```bash
kubectl port-forward -n camel-country-summary svc/camel-country-summary 8080:80
```

## Access Jaeger UI

```bash
kubectl port-forward -n camel-country-summary svc/jaeger 16686:16686
```

Then open:

- http://localhost:16686

## Notes for local clusters

- Docker Desktop Kubernetes: the local image tag `camel-country-summary:local` can usually be used directly.
- k3s: if your cluster does not see the local Docker image, import or push the image before applying the manifests.
