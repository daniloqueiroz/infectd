# Infectd test container
FROM      ubuntu:12.10
MAINTAINER Danilo Queiroz <dpenna.queiroz@gmail.com>

# Adds Infectd to the container
ADD build/distributions/infectd-1.0.tar /opt

# install java
RUN apt-get update
RUN apt-get install -y openjdk-7-jre-headless
RUN apt-get clean
RUN apt-get autoclean
RUN apt-get --purge autoremove

# Preparing to running
EXPOSE 8212
WORKDIR /tmp
USER daemon
#ENTRYPOINT ["/opt/infectd-1.0/bin/infectd"]
CMD ["/opt/infectd-1.0/bin/infectd", "--server"]
