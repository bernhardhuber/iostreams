/*
 * Copyright 2018 berni3.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.huberb.iostreams;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Xml sample data factory.
 *
 * @author berni3
 */
class SampleXmlData {

    @XmlRootElement
    public static class XmlParent {

        private String parentName;
        private List<XmlChild> children = new ArrayList<>();

        public XmlParent() {
            this("");
        }

        public XmlParent(String parentName) {
            this.parentName = parentName;
        }

        public String getParentName() {
            return parentName;
        }

        public void setParentName(String parentName) {
            this.parentName = parentName;
        }

        public List<XmlChild> getChildren() {
            return children;
        }

        public void setChildren(List<XmlChild> children) {
            this.children = children;
        }

        @Override
        public String toString() {
            return new ReflectionToStringBuilder(this, ToStringStyle.DEFAULT_STYLE).toString();
        }
    }

    public static class XmlChild {

        private String childName;

        public XmlChild() {
            this("");
        }

        public XmlChild(String childName) {
            this.childName = childName;
        }

        public String getChildName() {
            return childName;
        }

        public void setChildName(String childName) {
            this.childName = childName;
        }

        @Override
        public String toString() {
            return new ReflectionToStringBuilder(this, ToStringStyle.DEFAULT_STYLE).toString();
        }
    }

    static class XmlParentFactory {

        XmlParent createSmallSample() {
            XmlParent xmlParent = new XmlParent("smallParent");
            xmlParent.getChildren().add(new XmlChild("smallChild1"));
            return xmlParent;
        }

        XmlParent createSample(int n, String prefix) {
            XmlParent xmlParent = new XmlParent(prefix + "Parent");
            for (int i = 0; i < n; i++) {
                xmlParent.getChildren().add(new XmlChild(prefix + "Child" + i));
            }
            return xmlParent;
        }
    }
}
