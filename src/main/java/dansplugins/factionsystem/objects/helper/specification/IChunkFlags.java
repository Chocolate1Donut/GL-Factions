package dansplugins.factionsystem.objects.helper.specification;

/**
 * @author Daniel McCoy Stephenson
 */
public interface IChunkFlags {
    boolean getFlag(int x, int y);
    void setFlag(int x, int y, boolean f);
    void clear();
}
