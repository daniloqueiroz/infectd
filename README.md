# InfectD

**Under active developement - not usable ATM**

InfectD is a p2p system that aims provides membership, key-value storage for small data and events.

The goal is to enable to develop tools on top of it to manage applications cluster on cloud environments.

InfectD is completely distributed - means that there's no *master* node on the network - and it uses a gossip protocol to synchronize data between nodes. The system them generate events to notify the local node about the data changes and other applications can handle this events to perform any kind of tasks. A few use cases for InfectD could be to fire deployments on a cluster, or to configuration applications - either using the key-value short storage, or using the membership mechanism to detect network failures.

Due the decentralized architecture of InfectD, the data is eventually consistency, and nodes can falsely detect failures/partitions on the network.Anyway, the gossip protocol parameter can be tuned according to each environment particularities.

# Features
# Protocol
# Usage
## Server
## CLI
## Event Handlers
# Building
# How to contribute
# License
