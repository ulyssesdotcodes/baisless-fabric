using MLAgents;
using UnityEngine;

static class AcademyParameters {

    public static float FetchOrParse(Academy academy, string key) {
        if(academy.resetParameters.ContainsKey(key)) {
            return academy.resetParameters[key];
        } else {
            float.TryParse(key, out float val);
            return val;
        }
    }

    public static float Update(Academy academy, string key, float current) {
        if(academy.resetParameters.ContainsKey(key)) {
            return academy.resetParameters[key];
        } else {
            return current;
        }
    }
}
