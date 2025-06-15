public class TurtelAnimationControl {
    private final Turtel turtel;

    public TurtelAnimationControl(Turtel turtel) {
        this.turtel = turtel;
    }

    public void setTargetFPS(double fps) {
        turtel.targetFPS = fps;
    }
    public void setStepLength(long length) {
        turtel.stepLength = length;
    }
}
