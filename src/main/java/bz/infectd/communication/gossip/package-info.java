/**
 * Contains all the classes related to the protocol.
 * 
 * As InfectD uses Google's protocol buffers to encode the communication
 * this package holds the generated code as also other classes to handle the
 * encoding/decoding of the internal objects into the protocol buffer specific ones.
 * 
 * The **gossip** package contains the generated code to the messages used by the gossip protocol.
 * 
 * The {@link bz.infectd.communication.gossip.MessageFactory} provides methods to create protocol
 * buffer's **messages* from the internal objects, such as {@link bz.infectd.membership.Heartbeat}.
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
package bz.infectd.communication.gossip;