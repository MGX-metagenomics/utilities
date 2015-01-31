package de.cebitec.mgx.seqstorage.encoding;

import java.util.Arrays;

/**
 *
 * @author Patrick Blumenkamp
 */
public class QualityEncoder {
        
    public static byte[] decode(byte[] encoded) {
        byte encodedSize = encoded[0];
        byte minValue = encoded[1];
        byte[] decoded = new byte[(encoded.length-2)*8/encodedSize];
        byte decodedPos = 0;
        byte bytePos = 0;
        byte[] bitBuffer = new byte[14];
        
        for (int i=2; i < encoded.length; i++) {            
            byte currentByte = encoded[i];            
            for (int j = bytePos+7; j >= bytePos; j--) {                    
                bitBuffer[j] = (byte) (currentByte&1);
                currentByte = (byte) (currentByte >> 1);
            }
            
            bytePos += 8;
            
            while(bytePos >= encodedSize){
                byte k = bitBuffer[0];                
                for(int j=1; j<encodedSize; j++){
                    k = (byte) (k << 1);
                    k = (byte) (bitBuffer[j]|k);                    
                }
                
                //empty entry (padding)
                if (k == 0){                    
                    decoded = Arrays.copyOfRange(decoded, 0, decodedPos);
                    return decoded;
                } //empty entry (padding)
                
                decoded[decodedPos++] = (byte) (k+minValue);
                System.arraycopy(bitBuffer, encodedSize, bitBuffer, 0, 14-encodedSize);
                bytePos -= encodedSize;
            }
        }
        
        return decoded;
    }

    public static byte[] encode(byte[] quality) {
        int max = quality[0];
        int min = quality[0];
        for (int i = 1; i < quality.length; i++) {
            max = (max < quality[i]) ? quality[i] : max;
            min = (min > quality[i]) ? quality[i] : min;
        }
        min--;              //value 0 is used for padding
        max = max-min;
        byte compressedSize = (byte) (Math.log(max)/Math.log(2)+1);
        
        int encodedSize = (int) (quality.length*compressedSize/8.0+2.9);        
        byte[] encoded = new byte[encodedSize];
        encoded[0] = (byte) compressedSize;
        encoded[1] = (byte) min;
        byte[] bitBuffer = new byte[14];
        int resultPos = 2;
        int bytePos = 0;
        for (int i=0; i<quality.length; i++){
            byte currentByte = (byte) (quality[i]-min);
            for (int j=bytePos+compressedSize-1; j>=bytePos; j--){
                bitBuffer[j] = (byte) (currentByte&1);
                currentByte = (byte) (currentByte >> 1);
            }
            bytePos += compressedSize;
            
            if(bytePos > 7){
                byte k = bitBuffer[0];
                for(int j=1; j<8; j++){
                    k = (byte) (k << 1);
                    k = (byte) (bitBuffer[j]|k);                    
                }
                encoded[resultPos++] = k;
                System.arraycopy(bitBuffer, 8, bitBuffer, 0, 6);
                bytePos -= 8;
            }
        }
        if(bytePos != 0){
            for (int j=bytePos; j<8;j++)
                bitBuffer[j] = 0;
            
            byte k = bitBuffer[0];
            for(int j=1; j<8; j++){
                k = (byte) (k << 1);
                k = (byte) (bitBuffer[j]|k);                    
            }
            encoded[resultPos++] = k;            
        }
        
        return encoded;
    }
}
