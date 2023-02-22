package com.khalilmohamed.diff2xml.model;

import com.khalilmohamed.diff2xml.utils.diff_match_patch;
import lombok.Builder;
import lombok.Data;

import java.util.LinkedList;

@Data
@Builder
public class DiffObject {
    private String oldValue;
    private String newValue;
    private LinkedList<diff_match_patch.Diff> differences;
    private String xpathLocation;
}
