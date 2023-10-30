import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as ec2 from 'aws-cdk-lib/aws-ec2';

export class CdkDemoStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    // Configure a VPC that expands to 2 AZs
    const vpc = new ec2.Vpc(this, 'cdk-mainVPC', {
      maxAzs: 3,
      subnetConfiguration: [{
        cidrMask: 24,
        name: 'public',
        subnetType: ec2.SubnetType.PUBLIC,
      },
      {
        cidrMask: 24,
        name: 'private',
        subnetType: ec2.SubnetType.PRIVATE_ISOLATED,
      }
      ]
    });

    // Extract the subnet ids for post-processing

    const privateSubnets = vpc.selectSubnets({
      subnetType: ec2.SubnetType.PRIVATE_ISOLATED,
    });

    privateSubnets.availabilityZones.forEach(az => console.log('AZ=', az));
    privateSubnets.subnets.forEach(subnet => {
      console.log(`subnet=${subnet.node.id} subnetId=${subnet.subnetId} az=${subnet.availabilityZone}`);
    });
    privateSubnets.subnetIds.forEach(subnetId => console.log(`subnetId=${subnetId}`));

  }
}
