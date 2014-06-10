package de.cebitec.mgx.api;

import de.cebitec.gpms.core.MembershipI;
import de.cebitec.mgx.api.access.AccessBaseI;
import de.cebitec.mgx.api.access.AttributeAccessI;
import de.cebitec.mgx.api.access.DNAExtractAccessI;
import de.cebitec.mgx.api.access.FileAccessI;
import de.cebitec.mgx.api.access.HabitatAccessI;
import de.cebitec.mgx.api.access.JobAccessI;
import de.cebitec.mgx.api.access.MappingAccessI;
import de.cebitec.mgx.api.access.ObservationAccessI;
import de.cebitec.mgx.api.access.ReferenceAccessI;
import de.cebitec.mgx.api.access.SampleAccessI;
import de.cebitec.mgx.api.access.SeqRunAccessI;
import de.cebitec.mgx.api.access.SequenceAccessI;
import de.cebitec.mgx.api.access.StatisticsAccessI;
import de.cebitec.mgx.api.access.TaskAccessI;
import de.cebitec.mgx.api.access.TermAccessI;
import de.cebitec.mgx.api.access.ToolAccessI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.ModelBase;
import java.awt.datatransfer.DataFlavor;
import java.util.logging.Level;

/**
 *
 * @author sjaenick
 */
public abstract class MGXMasterI extends ModelBase<MGXMasterI> {

    public MGXMasterI(MGXMasterI master, DataFlavor dataflavor) {
        super(null, dataflavor);
        super.master = this;
    }

    public abstract MembershipI getMembership();

    public abstract String getProject();

    public abstract String getLogin();

    public abstract void log(Level lvl, String msg);

    public abstract HabitatAccessI Habitat();

    public abstract SampleAccessI Sample();

    public abstract DNAExtractAccessI DNAExtract();

    public abstract SeqRunAccessI SeqRun();

    public abstract ToolAccessI Tool();

    public abstract JobAccessI Job();

    public abstract AttributeAccessI Attribute();

    public abstract AccessBaseI<AttributeTypeI> AttributeType();

    public abstract ObservationAccessI Observation();

    public abstract SequenceAccessI Sequence();

    public abstract TermAccessI Term();

    public abstract FileAccessI File();

    public abstract ReferenceAccessI Reference();

    public abstract MappingAccessI Mapping();

    public abstract StatisticsAccessI Statistics();

    public abstract <T extends ModelBase> TaskAccessI<T> Task();

}
