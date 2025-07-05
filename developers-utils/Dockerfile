FROM registry.access.redhat.com/ubi7/ubi:latest

# Install Java 21 and OpenSSH server
RUN yum update -y && \
    yum install -y java-21-openjdk java-21-openjdk-devel openssh-server sudo && \
    yum clean all

# Set root password to 'root'
RUN echo "root:root" | chpasswd

# Setup SSH server
RUN mkdir /var/run/sshd && \
    ssh-keygen -A && \
    sed -i 's/#PermitRootLogin yes/PermitRootLogin yes/' /etc/ssh/sshd_config && \
    sed -i 's/#PasswordAuthentication yes/PasswordAuthentication yes/' /etc/ssh/sshd_config

# Expose SSH port
EXPOSE 22

# Run as root
USER root

# Start SSH daemon in foreground
CMD ["/usr/sbin/sshd", "-D"]
