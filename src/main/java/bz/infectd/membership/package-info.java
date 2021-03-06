/**
 * Contains the classes to deal with the membership of nodes - or in other 
 * words, track which nodes are online/offline. To track this the nodes
 * makes usage of heartbeats.
 * 
 * **Heartbeat**s are Lamport Clocks. Each node updates its heartbeat clock and
 * propagate it periodically - the heartbeat rounds. If we don't receive an
 * update from a node for several rounds, the node is mark as dead.
 * 
 * The **HeartbeatMonitor** is responsible by update it own *Heartbeat*
 * periodically and fire the propagation of the members list.
 * 
 * The **MembershipBoard** is responsible by keep track of external nodes heartbeat.
 *  
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
package bz.infectd.membership;