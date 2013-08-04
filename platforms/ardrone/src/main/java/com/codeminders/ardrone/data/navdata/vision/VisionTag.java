
package com.codeminders.ardrone.data.navdata.vision;

import com.codeminders.ardrone.data.navdata.NavDataFormatException;

public class VisionTag
{

    public VisionTag(VisionTagType type, Point position, Dimension dimensions, int distance)
    {
        super();
        this.type = type;
        this.position = position;
        this.dimensions = dimensions;
        this.distance = distance;
    }

    /**
     * @brief Values for the detection type on drone cameras.
     */
    public static enum VisionTagType
    {
        CAD_TYPE_HORIZONTL(0), /* Deprecated */
        CAD_TYPE_VERTICAL(1), /* Deprecated */
        CAD_TYPE_VISION(2), /* Detection of 2D horizontal tags on drone shells */
        CAD_TYPE_NONE(3), /* Detection disabled */
        CAD_TYPE_COCARDE(4), /* Detects a roundel under the drone */
        CAD_TYPE_ORIENTED_COCARDE(5), /*
                                       * Detects an oriented roundel under the
                                       * drone
                                       */
        CAD_TYPE_STRIPE(6), /* Detects a uniform stripe on the ground */
        CAD_TYPE_H_COCARDE(7), /* Detects a roundel in front of the drone */
        CAD_TYPE_H_ORIENTED_COCARDE(8), /*
                                         * Detects an oriented roundel in front
                                         * of the drone
                                         */
        CAD_TYPE_STRIPE_V(9), CAD_TYPE_MULTIPLE_DETECTION_MODE(10), /*
                                                                     * The drone
                                                                     * uses
                                                                     * several
                                                                     * detections
                                                                     * at the
                                                                     * same time
                                                                     */
        CAD_TYPE_NUM(11); /* Number of possible values for CAD_TYPE */

        private int value;

        private VisionTagType(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }

        public static VisionTagType fromInt(int v) throws NavDataFormatException
        {
            switch(v)
            {
            case 0:
                return VisionTagType.CAD_TYPE_HORIZONTL;
            case 1:
                return VisionTagType.CAD_TYPE_VERTICAL;
            case 2:
                return VisionTagType.CAD_TYPE_VISION;
            case 3:
                return VisionTagType.CAD_TYPE_NONE;
            case 4:
                return VisionTagType.CAD_TYPE_COCARDE;
            case 5:
                return VisionTagType.CAD_TYPE_ORIENTED_COCARDE;
            case 6:
                return VisionTagType.CAD_TYPE_STRIPE;
            case 7:
                return VisionTagType.CAD_TYPE_H_COCARDE;
            case 8:
                return VisionTagType.CAD_TYPE_H_ORIENTED_COCARDE;
            case 9:
                return VisionTagType.CAD_TYPE_STRIPE_V;
            case 10:
                return VisionTagType.CAD_TYPE_MULTIPLE_DETECTION_MODE;
            case 11:
                return VisionTagType.CAD_TYPE_NUM;
            default:
                throw new NavDataFormatException("Invalid vision tag type " + v);

            }
        }
    };

    // Type of the detected tag #i ; see the CAD_TYPE enumeration.
    private VisionTagType type;

    /**
     * X and Y coordinates of detected 2D-tag #i inside the picture, with (0, 0)
     * being the top-left corner, and (1000, 1000) the right-bottom corner
     * regardless the picture resolu- tion or the source camera.
     */
    private Point         position;

    /**
     * Width and height of the detection bounding-box (2D-tag #i), when
     * applicable.
     */
    private Dimension     dimensions;

    /**
     * Distance from camera to detected 2D-tag #i in centimeters, when
     * applicable.
     */
    private int           distance;

    public VisionTagType getType()
    {
        return type;
    }

    public Point getPosition()
    {
        return position;
    }

    public Dimension getDimensions()
    {
        return dimensions;
    }

    public int getDistance()
    {
        return distance;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("VisionTag [type=");
        builder.append(type);
        builder.append(", position=");
        builder.append(position);
        builder.append(", dimensions=");
        builder.append(dimensions);
        builder.append(", distance=");
        builder.append(distance);
        builder.append("]");
        return builder.toString();
    }
}
