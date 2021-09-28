package gasflow;

import static java.lang.Math.abs;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Volume extends GameObject {

    private final GasSimPane gsp;

    private Boolean hasFocus;
    private Rectangle gasColorRectangle;

    private double pressure;
    private double volume;
    private double moles;
    private double temperature;
    private double h2Percentage;
    private double o2Percentage;
    private double n2Percentage;

    private final double R = 0.00008205733847;

    private VolNode[] volNodes;

    public Volume(Vector2D position, Vector2D size, GasSimPane gsp) {
        super(position, new Vector2D(0, 0), new Vector2D(0, 0), size, null);

        this.gsp = gsp;

        hasFocus = false;
        gasColorRectangle = new Rectangle();
        gasColorRectangle = new Rectangle(this.getSize().getX(), this.getSize().getY());
        gasColorRectangle.setX(position.getX());
        gasColorRectangle.setY(position.getY());

        getRect().setFill(Color.ALICEBLUE);
        getRect().setStroke(Color.BLACK);
        getRect().setStrokeWidth(2);

        pressure = 0;
        volume = (this.getSize().getX() / 100) * (this.getSize().getY() / 100);
        moles = 0;
        temperature = 0;
        h2Percentage = 100;
        o2Percentage = 0;
        n2Percentage = 0;

        volNodes = new VolNode[]{
            new VolNode(new Vector2D(this.getPosition().getX(), this.getPosition().getY()), this),
            new VolNode(new Vector2D(this.getPosition().getX(), this.getPosition().getY()), this),
            new VolNode(new Vector2D(this.getPosition().getX(), this.getPosition().getY()), this),
            new VolNode(new Vector2D(this.getPosition().getX(), this.getPosition().getY()), this)
        };
    }

    public Rectangle getGasColorRectangle() {
        return gasColorRectangle;
    }

    @Override
    public void setPosition(Vector2D position) {
        super.setPosition(position);

        gasColorRectangle.setX(position.getX());
        gasColorRectangle.setY(position.getY());
    }

    @Override
    public void setSize(Vector2D size) {
        Vector2D absSize = new Vector2D(abs(size.getX()), abs(size.getY()));
        super.setSize(absSize);

        volume = this.getSize().getX() * this.getSize().getY() / 1000;

        gasColorRectangle.setWidth(getSize().getX());
        gasColorRectangle.setHeight(getSize().getY());
    }

    public void setFocus(Boolean hasFocus) {
        this.hasFocus = hasFocus;
    }

    public Boolean getFocus() {
        return hasFocus;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        if (pressure >= 0 && pressure <= 3600000) {
            this.pressure = pressure;
            if (this.pressure > 0) {
                if (temperature > 0) {
                    moles = this.pressure * volume / (R * temperature);
                }
                if (moles > 0) {
                    temperature = this.pressure * volume / (R * moles);
                }
                if (moles <= 0 && temperature <= 0) {
                    temperature = 273.15;
                    moles = this.pressure * volume / (R * temperature);
                }
            } else {
                moles = 0;
                temperature = 0;
            }
        } else {
            System.out.println("Pressure must be value between 0 and 3600000");
            gsp.instructionLabel.setText("Pressure must be value between 0 and 3600000");
        }
    }

    public double getVolume() {
        return volume;
    }

    public double getMoles() {
        return moles;
    }

    public void setMoles(double moles) {
        if (moles >= 0 && moles <= 7311960.28518 * volume) {
            this.moles = moles;
            pressure = this.moles * R * temperature / volume;
        } else {
            System.out.println("Moles must be value between 0 and " + 7311960.28518 * volume);
            gsp.instructionLabel.setText("Moles must be value between 0 and " + 7311960.28518 * volume);
        }
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        if (temperature >= 0 && temperature <= 6000) {
            this.temperature = temperature;
            pressure = this.moles * R * temperature / volume;
        } else {
            System.out.println("Temperature must be value between 0 and 6000");
            gsp.instructionLabel.setText("Temperature must be value between 0 and 6000");
        }
    }

    public double getH2Percentage() {
        return h2Percentage;
    }

    public void setH2Percentage(double h2Percentage) {
        if (h2Percentage < 0) {
            h2Percentage = 0;
        }
        if (h2Percentage > 100) {
            h2Percentage = 100;
        }
        if (this.h2Percentage == 100) {
            n2Percentage = 0.1;
            o2Percentage = 0.1;
        }
        if (h2Percentage >= 0 && h2Percentage <= 100) {
            //sets percentage of current gas
            this.h2Percentage = h2Percentage;
            //modifies percentages of other gases
            double otherGasRatio = o2Percentage / n2Percentage;
            n2Percentage = (100 - h2Percentage) / (otherGasRatio + 1);
            o2Percentage = 100 - h2Percentage - n2Percentage;
        } else {
            System.out.println("Gas percentage must be value between 0 and 100");
            gsp.instructionLabel.setText("Gas percentage must be value between 0 and 100");
        }
    }

    public double getO2Percentage() {
        return o2Percentage;
    }

    public void setO2Percentage(double o2Percentage) {
        if (o2Percentage < 0) {
            o2Percentage = 0;
        }
        if (o2Percentage > 100) {
            o2Percentage = 100;
        }
        if (this.o2Percentage == 100) {
            n2Percentage = 0.1;
            h2Percentage = 0.1;
        }
        if (o2Percentage >= 0 && o2Percentage <= 100) {
            //sets percentage of current gas
            this.o2Percentage = o2Percentage;
            //modifies percentages of other gases
            double otherGasRatio = h2Percentage / n2Percentage;
            n2Percentage = (100 - o2Percentage) / (otherGasRatio + 1);
            h2Percentage = 100 - n2Percentage - o2Percentage;
        } else {
            System.out.println("Gas percentage must be value between 0 and 100");
            gsp.instructionLabel.setText("Gas percentage must be value between 0 and 100");
        }
    }

    public double getN2Percentage() {
        return n2Percentage;
    }

    public void setN2Percentage(double n2Percentage) {
        if (n2Percentage < 0) {
            n2Percentage = 0;
        }
        if (n2Percentage > 100) {
            n2Percentage = 100;
        }
        if (this.n2Percentage == 100) {
            h2Percentage = 0.1;
            o2Percentage = 0.1;
        }
        if (n2Percentage >= 0 && n2Percentage <= 100) {
            //sets percentage of current gas
            this.n2Percentage = n2Percentage;
            //modifies percentages of other gases
            double otherGasRatio = h2Percentage / o2Percentage;
            o2Percentage = (100 - n2Percentage) / (otherGasRatio + 1);
            h2Percentage = 100 - o2Percentage - n2Percentage;
        } else {
            System.out.println("Gas percentage must be value between 0 and 100");
            gsp.instructionLabel.setText("Gas percentage must be value between 0 and 100");
        }
    }

    public VolNode[] getVolNodes() {
        return volNodes;
    }

    public void transferGas(double numOfMoles, Volume otherVol) {
        if (numOfMoles > 0) {
            //get energy per mole of initial gas from temperature
            double energyPerMole = 3 * R * getTemperature() / 2;
            //get amount of energy that will be transfered
            double energyTransfered = energyPerMole * numOfMoles;
            //add energy to final volume and update temperature
            double oldEnergy = 3 * R * otherVol.getTemperature() * otherVol.getMoles() / 2;
            double newEnergy = oldEnergy + energyTransfered;
            double newTemperature = 2 * newEnergy / (3 * R * (otherVol.getMoles() + numOfMoles));
            otherVol.setTemperature(newTemperature);
            //add moles of each kind to final volume then set percentage concentrations
            double newAmountH2 = (otherVol.getMoles() * otherVol.getH2Percentage() + numOfMoles * getH2Percentage()) / 100;
            double newAmountO2 = (otherVol.getMoles() * otherVol.getO2Percentage() + numOfMoles * getO2Percentage()) / 100;
            double newAmountN2 = (otherVol.getMoles() * otherVol.getN2Percentage() + numOfMoles * getN2Percentage()) / 100;

//            System.out.println("h2 amount to have in final one:" + newAmountH2);
//            System.out.println("o2 amount to have in final one:" + newAmountO2);
//            System.out.println("n2 amount to have in final one:" + newAmountN2);
            otherVol.setH2Percentage(100 * newAmountH2 / (newAmountH2 + newAmountO2 + newAmountN2));
            otherVol.setO2Percentage(100 * newAmountO2 / (newAmountH2 + newAmountO2 + newAmountN2));
            otherVol.setN2Percentage(100 * newAmountN2 / (newAmountH2 + newAmountO2 + newAmountN2));
            //set moles at target volume
            otherVol.setMoles(otherVol.getMoles() + numOfMoles);
            //remove moles at initial volume
            setMoles(getMoles() - numOfMoles);
        } else {
            System.out.println("Error: Cannot transfer non-positive amount of moles");
        }
    }

    public void diffuseGas(double numOfMoles, int gasType, Volume otherVol) {
        if (numOfMoles > 0) {
            //get energy per mole of initial gas from temperature
            double energyPerMole = 3 * R * getTemperature() / 2;
            //get amount of energy that will be transfered
            double energyTransfered = energyPerMole * numOfMoles;
            //add energy to final volume and update temperature
            double oldEnergy = 3 * R * otherVol.getTemperature() * otherVol.getMoles() / 2;
            double newEnergy = oldEnergy + energyTransfered;
            double newTemperature = 2 * newEnergy / (3 * R * (otherVol.getMoles() + numOfMoles));
            otherVol.setTemperature(newTemperature);
            //add moles of kind to final volume then set percentage concentrations and change concentration at initial volume
            System.out.println("NUMBER OF MOLE : " + numOfMoles);
            if (gasType == 0) {
                double newAmountH2 = otherVol.getMoles() * otherVol.getH2Percentage() / 100 + numOfMoles;
                System.out.println("New amount of H2 in other volume : " + newAmountH2);
                System.out.println("New percentage of H2 in other volume : " + 100 * (newAmountH2 / (numOfMoles + otherVol.getMoles())));
                otherVol.setH2Percentage(100 * (newAmountH2 / (numOfMoles + otherVol.getMoles())));

                double newAmountH2ForThisVolume = getMoles() * getH2Percentage() / 100 - numOfMoles;
                System.out.println("New amount of H2 in this volume : " + (getMoles() * getH2Percentage() / 100 - numOfMoles));
                System.out.println("New percentage of H2 in this volume : " + 100 * newAmountH2ForThisVolume / (getMoles() - numOfMoles));
                setH2Percentage(100 * newAmountH2ForThisVolume / (getMoles() - numOfMoles));
            } else if (gasType == 1) {
                double newAmountO2 = otherVol.getMoles() * otherVol.getO2Percentage() / 100 + numOfMoles;
                System.out.println("New amount of O2 in other volume : " + newAmountO2);
                System.out.println("New percentage of O2 in other volume : " + 100 * (newAmountO2 / (numOfMoles + otherVol.getMoles())));
                otherVol.setO2Percentage(100 * (newAmountO2 / (numOfMoles + otherVol.getMoles())));

                double newAmountO2ForThisVolume = getMoles() * getO2Percentage() / 100 - numOfMoles;
                System.out.println("New amount of O2 in this volume : " + (getMoles() * getH2Percentage() / 100 - numOfMoles));
                System.out.println("New percentage of O2 in this volume : " + 100 * newAmountO2ForThisVolume / (getMoles() - numOfMoles));
                setO2Percentage(100 * newAmountO2ForThisVolume / (getMoles() - numOfMoles));
            } else if (gasType == 2) {
                double newAmountN2 = otherVol.getMoles() * otherVol.getN2Percentage() / 100 + numOfMoles;
                System.out.println("New amount of N2 in other volume : " + newAmountN2);
                System.out.println("New percentage of N2 in other volume : " + 100 * (newAmountN2 / (numOfMoles + otherVol.getMoles())));
                otherVol.setN2Percentage(100 * (newAmountN2 / (numOfMoles + otherVol.getMoles())));

                double newAmountN2ForThisVolume = getMoles() * getN2Percentage() / 100 - numOfMoles;
                System.out.println("New amount of N2 in this volume : " + (getMoles() * getH2Percentage() / 100 - numOfMoles));
                System.out.println("New percentage of N2 in this volume : " + 100 * newAmountN2ForThisVolume / (getMoles() - numOfMoles));
                setN2Percentage(100 * newAmountN2ForThisVolume / (getMoles() - numOfMoles));
            }
            //set moles at target volume
            otherVol.setMoles(otherVol.getMoles() + numOfMoles);
            //remove moles at initial volume
            setMoles(getMoles() - numOfMoles);
        } else {
            System.out.println("Error: Cannot transfer non-positive amount of moles");
        }
    }

    public void transferTemp(double energy, Volume otherVol) {
        if (energy > 0) {
            //add energy to final volume and update temperature
            double otherOldEnergy = 3 * R * otherVol.getTemperature() * otherVol.getMoles() / 2;
            double otherNewEnergy = otherOldEnergy + energy;
            double otherNewTemperature = 2 * otherNewEnergy / (3 * R * (otherVol.getMoles()));
            otherVol.setTemperature(otherNewTemperature);

            //subtract energy from initial volume and update temperature
            double oldEnergy = 3 * R * getTemperature() * getMoles() / 2;
            double newEnergy = oldEnergy - energy;
            double newTemperature = 2 * newEnergy / (3 * R * (getMoles()));
            otherVol.setTemperature(newTemperature);
        }
    }

    public double getMass() {
        double h2Mass = h2Percentage * moles * 2.01588;     //in grams rn idk if thats what we want
        double o2Mass = o2Percentage * moles * 31.998;      //in grams rn idk if thats what we want
        double n2Mass = n2Percentage * moles * 28.0134;     //in grams rn idk if thats what we want

        return h2Mass + o2Mass + n2Mass;
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        if (h2Percentage > 100) {
            h2Percentage = 100;
        }
        if (o2Percentage > 100) {
            o2Percentage = 100;
        }
        if (n2Percentage > 100) {
            n2Percentage = 100;
        }
        if (h2Percentage < 0) {
            h2Percentage = 0;
        }
        if (o2Percentage < 0) {
            o2Percentage = 0;
        }
        if (n2Percentage < 0) {
            n2Percentage = 0;
        }

        double h2Color = h2Percentage / 100;
        double o2Color = o2Percentage / 100;
        double n2Color = n2Percentage / 100;
        double colorConcentration = moles / (100 * volume);
        if (colorConcentration > 0.8) {
            colorConcentration = 0.8;
        }
        Color gasColor = new Color(h2Color, o2Color, n2Color, colorConcentration);

        if (hasFocus) {
            getRect().setFill(Color.ANTIQUEWHITE);
            getRect().setStroke(Color.CRIMSON);
            getRect().setStrokeWidth(2);
        } else {
            getRect().setFill(Color.ALICEBLUE);
            getRect().setStroke(Color.BLACK);
            getRect().setStrokeWidth(2);
        }

        volNodes[0].setPosition(new Vector2D(this.getPosition().getX() - VolNode.getWidth() / 2, this.getPosition().getY() - VolNode.getWidth() / 2 + this.getSize().getY() / 2));                          //left
        volNodes[1].setPosition(new Vector2D(this.getPosition().getX() - VolNode.getWidth() / 2 + this.getSize().getX() / 2, this.getPosition().getY() - VolNode.getWidth() / 2));                          //top
        volNodes[2].setPosition(new Vector2D(this.getPosition().getX() - VolNode.getWidth() / 2 + this.getSize().getX(), this.getPosition().getY() - VolNode.getWidth() / 2 + this.getSize().getY() / 2));  //right
        volNodes[3].setPosition(new Vector2D(this.getPosition().getX() - VolNode.getWidth() / 2 + this.getSize().getX() / 2, this.getPosition().getY() - VolNode.getWidth() / 2 + this.getSize().getY()));  //bottom

        gasColorRectangle.setFill(gasColor);
        gasColorRectangle.toFront();
        gasColorRectangle.setStroke(new Color(0, 0, 0, 0));
        gasColorRectangle.setStrokeWidth(2);

        //gas flow
        double timeMult = 10000;
        for (VolNode n : volNodes) {
            if (n.getVolumeLink() != null) {
                if (n.getVolumeLink().getIsActivated()) {
                    Volume otherVolume = null;
                    if (n.getVolumeLink().getStartNode() == n && n.getVolumeLink().getEndNode() != null) {
                        otherVolume = n.getVolumeLink().getEndNode().getVolume();
                    } else if (n.getVolumeLink().getStartNode() != null) {
                        otherVolume = n.getVolumeLink().getStartNode().getVolume();
                    }
                    if (otherVolume != null) {
                        double pressureDifference = getPressure() - otherVolume.getPressure();
                        //find direction of gasflow (high pressure to low)(only compute transfer from high pressure volume)
                        if (pressureDifference > 0) {
                            //get velocity of gasFlow from pressure diference
                            double gasTransferVelocity = Math.sqrt(-(2.0 * (-pressureDifference) / (getMass() / getVolume())));
//                            System.out.println("Transfer Velocity : " + gasTransferVelocity + " = " + "√(2*" + -pressureDifference + "/(" + (getMass() / getVolume()) + "))");
                            double gasFlowRate = timeMult * VolumeLink.getSize() * gasTransferVelocity * (getMoles() / getVolume());   //moles per second
//                            System.out.println("Flow Rate : " + gasFlowRate);

                            double molesToTransfer = gasFlowRate * dt;

                            transferGas(molesToTransfer, otherVolume);
                        } else if (pressureDifference == 0) {
                            //equalize temperatures
                            double tempDifference = getTemperature() - otherVolume.getTemperature();
                            if (tempDifference > 0) {
                                System.out.println("Temperature Difference... Equalizing Temperatures");
                                double energyFlowRate = timeMult * VolumeLink.getSize() * 55 * tempDifference;     //watts per second
                                double energyToTransfer = energyFlowRate * dt;
                                transferTemp(energyToTransfer, otherVolume);
                            }
                            //equalize compositions
                            double D = 1;           //mass diffusivity
                            double M = 16;           //max number of moles to diffuse
                            //get concentration gradient
                            double h2ConcentrationGradient = getH2Percentage() - otherVolume.getH2Percentage();
                            if (h2ConcentrationGradient > 0.1) {
                                System.out.println("Hydrogen Concentration difference... Equalizing Concentration");
                                //get diffusionRates from gradients and constants
                                double h2DiffusionRate = timeMult * D * otherVolume.getTemperature() * otherVolume.getPressure() * h2ConcentrationGradient/100;
                                double h2ToTransfer = h2DiffusionRate * dt;
                                if(h2ToTransfer > M){
                                    h2ToTransfer = M;
                                }
                                if(h2ToTransfer > getMoles()){
                                    h2ToTransfer = getMoles();
                                }
                                diffuseGas(h2ToTransfer, 0, otherVolume);
                            }
                            //repeat previous steps for other gas
                            double o2ConcentrationGradient = getO2Percentage() - otherVolume.getO2Percentage();
                            if (o2ConcentrationGradient > 0.1) {
                                System.out.println("Oxygen Concentration difference... Equalizing Concentration");
                                //get diffusionRates from gradients and constants
                                double o2DiffusionRate = timeMult * D * otherVolume.getTemperature() * otherVolume.getPressure() * o2ConcentrationGradient/100;
                                double o2ToTransfer = o2DiffusionRate * dt;
                                if(o2ToTransfer > M){
                                    o2ToTransfer = M;
                                }
                                if(o2ToTransfer > getMoles()){
                                    o2ToTransfer = getMoles();
                                }
                                diffuseGas(o2ToTransfer, 1, otherVolume);
                            }
                            //repeat previous steps for other gas
                            double n2ConcentrationGradient = getN2Percentage() - otherVolume.getN2Percentage();
                            if (n2ConcentrationGradient > 0.1) {
                                System.out.println("Nitrogen Concentration difference... Equalizing Concentration");
                                //get diffusionRates from gradients and constants
                                double n2DiffusionRate = timeMult * D * otherVolume.getTemperature() * otherVolume.getPressure() * n2ConcentrationGradient/100;
                                double n2ToTransfer = n2DiffusionRate * dt;
                                if(n2ToTransfer > M){
                                    n2ToTransfer = M;
                                }
                                if(n2ToTransfer > getMoles()){
                                    n2ToTransfer = getMoles();
                                }
                                diffuseGas(n2ToTransfer, 2, otherVolume);
                            }
                        }
                    }
                }
            }
        }
    }
}
