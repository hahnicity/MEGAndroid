package gov.anl.coar.meg.pgp;

import android.content.Context;

import org.spongycastle.bcpg.sig.RevocationReasonTags;
import org.spongycastle.openpgp.PGPException;
import org.spongycastle.openpgp.PGPPrivateKey;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPSignature;
import org.spongycastle.openpgp.PGPSignatureGenerator;
import org.spongycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.spongycastle.openpgp.PGPUtil;
import org.spongycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.spongycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;

import java.io.FileOutputStream;
import java.io.IOException;

import gov.anl.coar.meg.Constants;

/**
 * Created by greg on 4/6/16.
 */
public class MEGRevocationKey {
    private PGPPublicKey mRevocationKey ;

    public MEGRevocationKey(PGPPublicKey revocationKey) {
        mRevocationKey = revocationKey;
    }

    private static PGPSignature generateSignature(
            PGPPublicKey publicKeyToRevoke,
            PGPPrivateKey privateKey,
            String description
    )
            throws PGPException
    {
        PGPSignatureGenerator signatureGenerator = new PGPSignatureGenerator(new JcaPGPContentSignerBuilder(
                publicKeyToRevoke.getAlgorithm(),
                PGPUtil.SHA1
        ).setProvider(Constants.SPONGY_CASTLE));
        signatureGenerator.init(PGPSignature.KEY_REVOCATION, privateKey);
        PGPSignatureSubpacketGenerator subGenerator = new PGPSignatureSubpacketGenerator();
        byte[] fingerprint = new JcaKeyFingerprintCalculator().calculateFingerprint(publicKeyToRevoke.getPublicKeyPacket());
        subGenerator.setRevocationKey(true, publicKeyToRevoke.getAlgorithm(), fingerprint);
        subGenerator.setRevocationReason(true, RevocationReasonTags.NO_REASON, description);
        // TODO we should generate the creation time as well.
        signatureGenerator.setHashedSubpackets(subGenerator.generate());
        return signatureGenerator.generateCertification(publicKeyToRevoke);
    }

    protected static MEGRevocationKey generate(
            PGPPublicKey publicKeyToRevoke,
            PGPPrivateKey privateKey,
            String description
    )
            throws PGPException
    {
        PGPSignature sig = generateSignature(publicKeyToRevoke, privateKey, description);
        PGPPublicKey revocation = PGPPublicKey.addCertification(publicKeyToRevoke, sig);
        return new MEGRevocationKey(revocation);
    }

    protected void toFile(
            Context context
    )
            throws IOException
    {
        FileOutputStream pubKeyRingOutput = context.openFileOutput(
                Constants.REVOCATIONKEY_FILENAME, Context.MODE_PRIVATE
        );
        mRevocationKey.encode(pubKeyRingOutput);
        pubKeyRingOutput.close();
    }

    public static MEGRevocationKey fromFile(
            Context context
    )
            throws IOException, PGPException
    {
        MEGPublicKeyRing ring = MEGPublicKeyRing.fromFile(context, Constants.REVOCATIONKEY_FILENAME);
        if (ring == null)
            throw new IllegalArgumentException("Was unable to retrieve revocation key from file!");
        return new MEGRevocationKey(ring.getRevocationKey());
    }

    public PGPPublicKey getKey() {
        return mRevocationKey;
    }
}
