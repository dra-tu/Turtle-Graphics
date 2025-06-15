public class TurtelAnimationControll {
    private Turtel turtel;

    public TurtelAnimationControll(Turtel turtel) {
        this.turtel = turtel;
    }

    public void setTartgetFPS(double fps) {
        turtel.targetFPS = fps;
    }
    public void setStepLenght(long lenght) {
        turtel.stepLength = lenght;
    }
}
