---
description: 生产部署环境配置
globs: 
alwaysApply: false
---
# production deploy env
- os: linux ubuntu 24.04
- container: docker and ocker-compose
- hardware：2C4G
- domain：yifajucai.com
## other softwares
- certbot
## docker services
- api_service: custom playwright + openjdk23
- db: postgresql 17.4
- cache: redis 17
- oss: MinIO 2025.03
- webserver: wNginx
## docker volumns
- root: /opt/dl
- nginx: /opt/dl/nginx
- nginx html dir: /opt/dl/nginx/html/dist
## securify rules
- export_port_rules：enabled: 21,22,80,443,9001,5432,6379 disabled: all
